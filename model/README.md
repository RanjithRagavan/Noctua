# Noctua on-device model

`export_readiness_forecaster.py` (also available as the step-by-step notebook
`export_readiness_forecaster.ipynb`) exports a tiny MLP (8 → 32 → 16 → 1) to
an ExecuTorch `.pte` program that predicts **tomorrow's readiness score** from
normalized Oura features — fully on-device, ~1 MB RAM, milliseconds of CPU.

```bash
python -m venv .venv && source .venv/bin/activate
pip install torch executorch
python export_readiness_forecaster.py
# → readiness_forecaster.pte
```

A **pre-exported, runtime-validated** program is checked in at
[`sample/readiness_forecaster.pte`](sample/readiness_forecaster.pte) (~6 KB).
Validation output from the ExecuTorch runtime:

```
neutral input → 75.1     strong input → 87.4     strained input → 66.3
```

Ship the `.pte` in your app (assets or internal storage) and wire it up:

```kotlin
val ai = NoctuaAI(forecaster = ExecuTorchForecaster(modelFile.absolutePath))
```

If the runtime or file is missing, `NoctuaAI` silently falls back to the
bundled `LinearHeuristicForecaster`, so the app never breaks.

## Model card

| | |
|---|---|
| **Task** | Tabular regression: 8 wellness features → next-day readiness score (0–100) |
| **Architecture** | MLP 8 → 32 → 16 → 1 (ReLU), 833 parameters |
| **Base model** | **None.** Trained from scratch — not distilled, pruned, or quantized from a larger model |
| **Training data (v0.1)** | 2,048 synthetic samples encoding a heuristic prior (same directions as `LinearHeuristicForecaster`); warm-start MSE 6.2 after 1,500 epochs |
| **Format** | ExecuTorch `.pte` (`torch.export → to_edge → to_executorch`), ~6 KB — ~3.3 KB weights + graph/metadata |
| **Contract** | input `float32 [1, 8]` (see `WellnessFeatures.toVector()`), output `float32 [1, 1]` |
| **Runtime** | ExecuTorch on-device (no Python, no PyTorch on the phone); <5 ms inference, ~1 MB RAM |
| **Fallback** | `LinearHeuristicForecaster` — zero-dependency linear model over the same vector |
| **Validated** | ExecuTorch runtime execution: neutral 75.1 / strong 87.4 / strained 66.3 ✓ |
| **Limitations** | Weights encode a physiological *prior*, not a clinically validated model; not medical advice; personalize via retraining (below) |

### Why is the model only 6 KB?

Model size = parameters × bytes per parameter — nothing else:

| Layer | Weights | Biases | Params |
|---|---|---|---|
| Linear 8→32 | 256 | 32 | 288 |
| Linear 32→16 | 512 | 16 | 528 |
| Linear 16→1 | 16 | 1 | 17 |
| **Total** | | | **833** |

833 × 4 bytes (float32) ≈ 3.3 KB of weights; the rest of the 6 KB is the
serialized computation graph and memory plan. Predicting one score from eight
well-engineered features simply does not require more capacity — the
information lives in the features (sleep debt, HRV z-score vs personal
baseline), not in network size. A larger model would memorize noise.

Contrast with the roadmap **LLM sleep coach**: that use case *does* start from
a pre-trained base model (e.g. Llama 3.2 1B, ~1.2 B parameters), quantized and
exported to a ~700 MB `.pte` via the same toolchain. Same file format, two
different worlds: purpose-built tiny regression vs. compressed foundation
model. Choosing 833 parameters where 833 suffice is the design decision.

## Why on-device?

Biometric time series are among the most sensitive data a person generates.
Running inference on-device means the raw HRV/sleep/temperature history never
leaves the phone — no cloud round-trips, no third-party processors, GDPR/HIPAA
surface area dramatically reduced. That is the core design tenet of Noctua.

## Retraining on personal history

The warm-start in the script only teaches the network the heuristic prior.
For a personalized forecaster, collect N weeks of the user's
`WellnessFeatures.toVector()` + next-day readiness score and fine-tune before
export. The export contract (`float32 [1,8] → [1,1]`) does not change.
