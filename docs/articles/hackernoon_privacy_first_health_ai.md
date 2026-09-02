# Article 2 — HackerNoon

> Note: "HackerRank" is a coding-challenge platform with no blogging surface —
> this draft targets **HackerNoon**, the natural technical fit for this piece.

**Working title:** *Privacy-First Health AI: Running ExecuTorch Models on Oura Ring Data in Android*

**Tags:** #android #kotlin #executorch #on-device-ml #privacy #wearables #oura-ring #open-source

---

## TL;DR

I open-sourced **Noctua** (https://github.com/RanjithRagavan/Noctua), a Kotlin
SDK that pulls Oura Ring data via the Oura API v2 and computes wellness
insights + a next-day readiness forecast **entirely on-device** using
ExecuTorch — with a deterministic fallback when the ML runtime isn't present.
This article is the deep technical walkthrough: the API client design, the
feature engineering, the reflection bridge that keeps the AI module pure-JVM,
and the PyTorch→ExecuTorch export path.

## 1. The Oura API v2 surface

The v2 API is a REST API at `https://api.ouraring.com/v2`. Everything
user-scoped lives under `/v2/usercollection/{type}`:

- Daily summaries: `daily_sleep`, `daily_readiness`, `daily_activity`,
  `daily_spo2`, `daily_stress`, `daily_resilience`, `daily_cardiovascular_age`,
  `vO2_max`
- Detailed periods: `sleep` (with embedded HR/HRV streams), `sleep_time`,
  `rest_mode_period`
- Time series: `heartrate` (ISO-8601 datetime params)
- Events: `workout`, `session`, `tag`, `enhanced_tag`

Auth is OAuth2 (authorize at `cloud.ouraring.com/oauth/authorize`, token at
`api.ouraring.com/oauth/token`), with a client-side `response_type=token` flow
that works nicely from a mobile Custom Tab. Rate limit is generous
(~5000 req / 5 min). There's also a **sandbox** mirror at
`/v2/sandbox/usercollection/...` returning deterministic data — great for CI.

Multi-document responses look like `{ "data": [...], "next_token": "..." }`,
so `noctua-core` follows the token for you:

```kotlin
private suspend fun <T> fetchAll(
    call: suspend (String?) -> MultiDocumentResponse<T>,
): List<T> {
    val out = ArrayList<T>()
    var token: String? = null
    do {
        val page = call(token)
        out += page.data
        token = page.nextToken
    } while (token != null)
    return out
}
```

HTTP failures map to sealed `OuraException` subtypes (`Unauthorized`,
`RateLimited`, `Http`, `Network`, `Serialization`), so callers can
`when` over failure modes instead of parsing status codes.

## 2. Feature engineering: from biometrics to a vector

`noctua-ai` distills 21 days of ring data into 8 normalized signals:

| Feature | Meaning |
|---|---|
| sleep debt | Σ(target − actual sleep) over 7 nights |
| HRV z-score | last night's avg HRV vs 14-night personal baseline |
| readiness trend | least-squares slope over 7 days |
| readiness/sleep 7-day means | normalized around 75 |
| temperature deviation | °C off baseline (illness early-warning) |
| sleep efficiency | % of time-in-bed asleep |
| steps | normalized around 8k |

The HRV z-score is the most valuable single signal: a night 1.5σ below your
*own* baseline is a far better strain indicator than any population threshold.

## 3. Two engines, one report

**Heuristic engine** — transparent rules with confidence scores:

```kotlin
when {
    z <= -1.5f -> Insight(STRAIN, WARNING, "HRV below your baseline", ...)
    z >=  1.5f -> Insight(RECOVERY, POSITIVE, "HRV rebound", ...)
}
```

Every insight cites the feature that fired it. Explainability is a feature,
not a nice-to-have, when the output nudges someone's training decisions.

**Neural forecaster** — predicts tomorrow's readiness from the feature vector.
The ExecuTorch runtime is bridged *reflectively* so the module stays pure-JVM
(and unit-testable on any machine):

```kotlin
class ExecuTorchForecaster(private val modelPath: String) : ReadinessForecaster {
    private val module: Any? by lazy {
        runCatching {
            Class.forName("org.pytorch.executorch.Module")
                .getMethod("load", String::class.java)
                .invoke(null, modelPath)
        }.getOrNull()
    }
    // predictTomorrow() builds a [1,8] float tensor, calls forward(),
    // returns null on any failure → NoctuaAI falls back to the linear model
}
```

`NoctuaAI.analyze()` runs all engines, dedupes, ranks by severity, and always
returns *something* — the `LinearHeuristicForecaster` guarantees a forecast
with ≥3 days of history even with no ML runtime at all.

Note the split: the *library* keeps ExecuTorch optional via the reflective
bridge, but the **example app ships `org.pytorch:executorch-android:1.4.0`
and the pre-exported `readiness_forecaster.pte` by default** — so anyone who
clones the repo gets real neural inference on first launch, with a
"neural · ExecuTorch" badge on the forecast card showing which engine
answered. Fallback exists so *your* app can choose, not so the demo can
cheat.

## 4. Exporting the model

`model/export_readiness_forecaster.py` warm-starts a tiny MLP on a synthetic
heuristic prior (so the artifact works end-to-end out of the box), then:

```python
aten = torch.export.export(model, (torch.rand(1, 8),))
program = to_edge(aten).to_executorch()
open("readiness_forecaster.pte", "wb").write(program.buffer)
```

Contract: `float32 [1,8] → [1,1]`, readiness in [0,100]. Retrain on the user's
own history for personalization — the contract doesn't change.

## 5. Results and what's next

![Noctua dashboard (left) and on-device AI Coach feed (right), running in demo mode](https://raw.githubusercontent.com/RanjithRagavan/Noctua/main/docs/screenshots/dashboard.png)

- Full Gradle build green: unit tests pass, example APK (~17 MB) assembles cleanly
- Verified against a live Oura account over OAuth2: readiness, sleep, activity,
  and detailed sleep periods all flow through the pipeline end-to-end
- Forecast + insights compute in milliseconds, offline, with zero data egress
- The example app bundles `executorch-android:1.4.0` + the pre-exported
  `.pte` — the "neural · ExecuTorch" badge on the forecast card confirms which
  engine answered
- Want every formula? `docs/HOW_THE_AI_WORKS.md` in the repo traces one real
  data point from Oura JSON through feature extraction to each layer of the
  .pte execution stack
- Next: on-device LLM sleep coach via the ExecuTorch Llama runner, nightly
  on-device fine-tuning, Health Connect write-back

Star/fork: **https://github.com/RanjithRagavan/Noctua** — issues and PRs
welcome; the heuristics are deliberately readable if you want a first
contribution.
