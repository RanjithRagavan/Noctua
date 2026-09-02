# Article 1 — Medium

**Working title:** *I Built an On-Device AI Coach for My Oura Ring — No Cloud Required*

**Subtitle:** Noctua is an open-source Kotlin SDK that pairs the Oura API v2 with ExecuTorch, so your biometric data never leaves your phone.

**Tags:** Android, Kotlin, Jetpack Compose, On-Device AI, Privacy, Oura, ExecuTorch, Open Source

---

A few months ago I caught myself doing something odd. My Oura app is great at measurement — every morning it hands me a readiness score — but it doesn't coach: it never answers the question I actually have, *"so should I train hard today, or recover?"* So every morning I'd paste my scores and biometrics into a cloud chatbot to get that answer. It worked surprisingly well — and then it hit me: **I was voluntarily uploading my most intimate biometric history to a cloud LLM to answer a question a phone can answer locally.**

So I built the thing I wanted: **Noctua** 🦉 — an open-source Android SDK that combines a complete typed client for the Oura API v2 with an on-device AI layer that generates explainable wellness insights and predicts tomorrow's readiness score. All inference happens on-device. Nothing is uploaded.

Repo: https://github.com/RanjithRagavan/Noctua

![The Noctua example app: score rings, tomorrow's readiness forecast, and a 14-day readiness trend — rendered in demo mode](https://raw.githubusercontent.com/RanjithRagavan/Noctua/main/docs/screenshots/dashboard.png)

*The example app's dashboard. Every number on this screen was computed on-device.*

## The architecture in one picture

Noctua is three Gradle modules:

- **`noctua-core`** — a coroutine-first, fully typed Kotlin client for the Oura API v2. OAuth2 helpers, auto-refreshing tokens, every `usercollection` endpoint, transparent pagination, typed errors. It's pure JVM, so it works on Android *and* on a backend.
- **`noctua-ai`** — the part I'm most proud of. Feature extraction (sleep debt, HRV z-score against your personal 14-night baseline, readiness trend), an explainable heuristic insight engine, and a neural readiness forecaster running on **ExecuTorch** with a zero-dependency fallback.
- **`example-app`** — a Material 3 Compose app with score rings, a readiness trend chart, and an AI coach feed. It boots into demo mode with a synthetic 21-day dataset, so you can try it without owning a ring.

## The code is the boring part (in a good way)

Fetching your last three weeks of Oura data:

```kotlin
val oura = OuraClient.Builder().token(token).build()
val snapshot = WellnessSnapshot(
    readiness = oura.dailyReadiness(start, end),
    sleep = oura.dailySleep(start, end),
    activity = oura.dailyActivity(start, end),
    sleepPeriods = oura.sleep(start, end),
)
```

And the on-device analysis:

```kotlin
val report = NoctuaAI().analyze(snapshot)
println(report.forecastedReadiness)      // 74 — tomorrow's predicted readiness
report.insights.forEach { println(it.title) }
// "Sleep debt accumulating", "HRV below your baseline", ...
```

![The AI Coach feed: explainable insights with confidence scores, generated on-device](https://raw.githubusercontent.com/RanjithRagavan/Noctua/main/docs/screenshots/ai_coach.png)

*What the AI Coach feed renders: each card is one rule firing on real features, with its confidence shown.*

Every insight is traceable to the features that triggered it — no black box, no hallucinated medical advice, because the rules are deterministic and unit-tested.

## The neural part: ExecuTorch, out of the box

This is the part I want to underline, because "on-device AI" usually means "good luck integrating it." With Noctua it doesn't:

**The example app already ships the ExecuTorch runtime (`org.pytorch:executorch-android:1.4.0`) and the pre-exported `readiness_forecaster.pte`.** Clone, build, and the neural forecaster is running — the forecast card literally shows a **"neural · ExecuTorch"** badge when the .pte model produced the prediction, and "linear fallback" if the runtime were ever missing. No setup, no model downloads, no server.

The model itself is deliberately tiny — an 8 → 32 → 16 → 1 MLP, exported from PyTorch, and the resulting `.pte` is only a few kilobytes. People are surprised an "AI model" can be 6 KB; the answer is that the .pte isn't a compressed giant, it's the *whole* model — the architecture is small by design, because 8 physiological features don't need a transformer. The base model is defined and warm-started in PyTorch (`model/export_readiness_forecaster.py`), then exported through the ExecuTorch path:

```bash
cd model && pip install torch executorch
python export_readiness_forecaster.py
# torch.export → to_edge → to_executorch → readiness_forecaster.pte
```

```kotlin
val ai = NoctuaAI(forecaster = ExecuTorchForecaster(pteFile.absolutePath))
```

The contract is `float32 [1,8] → [1,1]` (readiness in 0–100), so you can retrain the base model on a user's own history and drop in the new .pte without touching app code — personalization as a local file, not a cloud subscription.

Inside the library, the ExecuTorch runtime is still bridged reflectively: apps that ship `org.pytorch:executorch-android` get neural inference; everyone else silently falls back to a transparent linear model. The library compiles and passes its unit tests either way — dependency optionality without dependency hell.

📖 If you want the full "how does a number become a prediction" trace — every formula from raw Oura JSON through feature extraction to each layer of the .pte execution stack — I wrote it up in the repo: **[docs/HOW_THE_AI_WORKS.md](https://github.com/RanjithRagavan/Noctua/blob/main/docs/HOW_THE_AI_WORKS.md)**.

## Why this matters beyond one app

Health data is the most sensitive data most of us generate. "Send it to a cloud model" should be the last resort, not the default. On-device runtimes — ExecuTorch on Android, Core ML on iOS — are now good enough that the privacy-respecting architecture is also the *faster* one. Noctua is my proof of that, and my small contribution back to the open-source ecosystem that taught me everything I know.

Try it, fork it, break it: **https://github.com/RanjithRagavan/Noctua**

---

*Setup from zero to running app: clone → `./gradlew :example-app:installDebug` → demo mode works immediately with the neural forecaster live. For live data, the Connect tab supports both OAuth2 sign-in (drop your client ID in `local.properties` as `OURA_CLIENT_ID=...` — the secret stays out of the APK entirely) and a Personal Access Token. Full instructions in the README.*
