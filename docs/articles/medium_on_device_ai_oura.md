# Article 1 — Medium

**Working title:** *I Built an On-Device AI Coach for My Oura Ring — No Cloud Required*

**Subtitle:** Noctua is an open-source Kotlin SDK that pairs the Oura API v2 with ExecuTorch, so your biometric data never leaves your phone.

**Tags:** Android, Kotlin, Jetpack Compose, On-Device AI, Privacy, Oura, ExecuTorch, Open Source

---

A few months ago I caught myself doing something odd: every morning I'd open the Oura app, screenshot my readiness score, and paste it into a chatbot to ask "should I train hard today?" It worked surprisingly well — and then it hit me: **I was voluntarily uploading my most intimate biometric history to a cloud LLM to answer a question a phone can answer locally.**

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

## The neural part: ExecuTorch

For the readiness forecast I exported a tiny MLP (8 → 32 → 16 → 1, runs in single-digit milliseconds) to a `.pte` program:

```bash
cd model && pip install torch executorch
python export_readiness_forecaster.py   # → readiness_forecaster.pte
```

```kotlin
val ai = NoctuaAI(forecaster = ExecuTorchForecaster(pteFile.absolutePath))
```

The ExecuTorch runtime is loaded reflectively: apps that ship `org.pytorch:executorch-android` get neural inference; everyone else silently falls back to a transparent linear model. The library compiles and passes its 16 unit tests either way.

## Why this matters beyond one app

Health data is the most sensitive data most of us generate. "Send it to a cloud model" should be the last resort, not the default. On-device runtimes — ExecuTorch on Android, Core ML on iOS — are now good enough that the privacy-respecting architecture is also the *faster* one. Noctua is my proof of that, and my small contribution back to the open-source ecosystem that taught me everything I know.

Try it, fork it, break it: **https://github.com/RanjithRagavan/Noctua**

---

*Setup from zero to running app: clone → `./gradlew :example-app:installDebug` → demo mode works immediately; paste a Personal Access Token in the Connect tab for live data. Full instructions in the README.*
