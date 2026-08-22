# Noctua on-device model

`export_readiness_forecaster.py` exports a tiny MLP (8 → 32 → 16 → 1) to an
ExecuTorch `.pte` program that predicts **tomorrow's readiness score** from
normalized Oura features — fully on-device, ~1 MB RAM, milliseconds of CPU.

```bash
pip install torch executorch
python export_readiness_forecaster.py
# → readiness_forecaster.pte
```

Ship the `.pte` in your app (assets or internal storage) and wire it up:

```kotlin
val ai = NoctuaAI(forecaster = ExecuTorchForecaster(modelFile.absolutePath))
```

If the runtime or file is missing, `NoctuaAI` silently falls back to the
bundled `LinearHeuristicForecaster`, so the app never breaks.

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
