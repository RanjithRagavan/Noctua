# How Noctua's AI works — a complete walkthrough

This document traces one real piece of data from the Oura cloud to the number
on your screen, with the actual formulas and the actual execution path. No
abstractions skipped.

Pipeline overview:

```
Oura API v2 ──► WellnessSnapshot ──► FeatureExtractor ──► WellnessFeatures
                                          │
                    ┌─────────────────────┴─────────────────────┐
                    ▼                                           ▼
        HeuristicInsightEngine                     ExecuTorchForecaster
        (explainable rules)                        (833-param MLP, .pte)
                    │                                           │
                    └─────────────────────┬─────────────────────┘
                                          ▼
                              NoctuaReport (insights + forecast)
                                          ▼
                                   Compose UI cards
```

---

## Stage 1 — Raw data arrives

Four concurrent calls to the Oura API v2 (trailing 21 days):

| Collection | Documents used |
|---|---|
| `daily_readiness` | day, score, temperature_deviation, contributors |
| `daily_sleep` | day, score, contributors |
| `daily_activity` | day, steps, calories, score |
| `sleep` (detailed periods) | total_sleep_duration, average_hrv, efficiency, stages, bedtimes |

Everything downstream is pure, deterministic math on these lists. The AI
layer never makes another network call — it has no network capability at all.

## Stage 2 — Feature extraction

`FeatureExtractor` (pure functions, unit-tested) distills the history into
physiologically meaningful signals:

**Sleep debt (hours)** — cumulative shortfall vs. an 8 h target over the last
7 long sleeps:

```
debt_seconds = Σ max over periods of (28_800 − total_sleep_duration)
sleepDebtHours = debt_seconds / 3600, clamped to ±24
```

**HRV z-score** — last night's average HRV measured against *your own*
14-night baseline, not a population average:

```
baseline = average_hrv of the 14 long sleeps before last night
z = (last_night − mean(baseline)) / stddev(baseline)     (requires ≥ 5 nights)
```

z ≤ −1.5σ is an early strain signal even when the absolute value looks
"normal" — personalization is the point.

**Readiness trend** — least-squares slope over the last 7 daily scores:

```
slope = Σ((xᵢ − x̄)(yᵢ − ȳ)) / Σ(xᵢ − x̄)²      (points/day)
```

Plus: 7-day readiness mean, 7-day sleep-score mean, latest temperature
deviation (°C), latest sleep efficiency, latest steps.

## Stage 3a — Heuristic insight engine

Every insight is a readable rule with a confidence score. Examples from
`HeuristicInsightEngine`:

```kotlin
when {
    z <= -1.5f -> Insight(STRAIN, WARNING,  "HRV below your baseline",  confidence = 80)
    z >=  1.5f -> Insight(RECOVERY, POSITIVE, "HRV rebound",            confidence = 75)
}
sleepDebtHours >= 2f  -> "Sleep debt accumulating"  (WARNING, 88%)
readinessMean7d >= 85 -> "Peak recovery window"     (POSITIVE, 82%)
sleepEfficiency < 80  -> "Restless night"           (INFO, 65%)
temperatureDeviation >= +0.5°C -> "Elevated body temperature" (ALERT, 84%)
```

Insights are deduplicated and ranked by severity — that ranked list is the
AI Coach feed. Explainability is a design requirement: every card can name
the exact feature values that triggered it.

## Stage 3b — Neural readiness forecast

**Step A — the contract vector.** `WellnessFeatures.toVector()` normalizes
each feature to roughly [−1, 1]:

```kotlin
floatArrayOf(
    (sleepDebtHours / 10f),                 // debt hurts
    (hrvZScore / 3f),                       // rebound helps
    (readinessTrend / 5f),                  // momentum
    ((readinessMean7d - 75f) / 25f),        // level vs 75
    ((sleepScoreMean7d - 75f) / 25f),
    (temperatureDeviation / 2f),            // illness signal
    ((sleepEfficiency - 85f) / 15f),
    ((steps - 8000f) / 8000f),
)  // shape [1, 8]
```

**Step B — the MLP.** 833 parameters, three matmuls:

```
h1 = ReLU(W1·x  + b1)     W1: 32×8   — 288 params
h2 = ReLU(W2·h1 + b2)     W2: 16×32  — 528 params
y  = W3·h2 + b3           W3: 1×16   —  17 params  → readiness (0–100)
```

The warm-start weights encode the heuristic prior (debt ↓, HRV z ↑,
temperature ↑↓). The hidden layers let the network express *interactions* a
linear model cannot — e.g. "sleep debt matters more when HRV is also low."
Personal fine-tuning (roadmap) replaces the prior with the user's own
history; the `[1,8] → [1,1]` contract never changes.

---

## How the .pte executes on-device

Full stack, file to number:

```
readiness_forecaster.pte (6 KB, in APK assets/)
   │  ① first launch — copied to filesDir (ExecuTorch needs a real,
   │    mmap-able file path; assets/ entries are not file paths)
   ▼
ExecuTorchForecaster (noctua-ai) — reflection bridge:
   Class.forName("org.pytorch.executorch.Module")
       .getMethod("load", String).invoke(null, path)
   │  ② JNI boundary
   ▼
ExecuTorch C++ runtime (bundled in executorch-android AAR)
   • parses .pte: serialized graph + weights + memory plan
   • loads portable kernels (addmm, relu)
   • allocates pre-planned tensor arena (~1 MB)
   │  ③ forward pass
   ▼
features.toVector() → float[8]
Tensor.fromBlob(arr, [1, 8])      // zero-copy view
module.forward(EValue.from(t))    // C++: addmm → relu → addmm → relu → addmm
outputs[0].toTensor().getDataAsFloatArray() → coerceIn(0, 100) → forecast
```

Design properties worth calling out:

1. **No Python, no PyTorch on the phone.** PyTorch is the authoring tool,
   used once at export time. The device runs a small C++ engine executing a
   static graph — microseconds of compute, no battery impact, works offline.
2. **Reflection keeps the library pure-JVM.** `noctua-ai` never imports
   ExecuTorch classes, so it compiles and its unit tests run anywhere. If the
   runtime or file is absent, `isAvailable()` is false and `NoctuaAI` falls
   back to the transparent `LinearHeuristicForecaster` — the app never breaks.
3. **Runtime/export version pairing matters.** The shipped `.pte` was
   exported by pip `executorch 1.4.1` and runs on the `1.4.0` Android
   runtime; keep the minor version aligned when upgrading either side.
4. **Privacy by construction.** Stages 2–3 have no I/O. The only network
   traffic in the entire system is stage 1 talking to `api.ouraring.com`
   over TLS. There is nothing to leak because nothing leaves.

---

## Reproduce the numbers yourself

```bash
cd model
python -m venv .venv && source .venv/bin/activate
pip install torch executorch
python export_readiness_forecaster.py     # trains + exports + writes .pte
```

Then validate the artifact through the same runtime the phone uses:

```python
from executorch.runtime import Runtime
m = Runtime.get().load_program("readiness_forecaster.pte").load_method("forward")
m.execute([torch.zeros(1, 8)])            # neutral user  → ~75
```

See also: [model card](model/README.md#model-card) for the parameter math
behind the 6 KB size, and the step-by-step notebook
[export_readiness_forecaster.ipynb](model/export_readiness_forecaster.ipynb).
