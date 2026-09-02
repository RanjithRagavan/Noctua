# LinkedIn long-form article

**Title:** *Your Health Data Shouldn't Need a Cloud: Building an On-Device AI Coach for Oura Ring*

**Format:** LinkedIn Article (long-form, ~1,200 words). Hero image: `docs/screenshots/dashboard.png`. Inline image: `ai_coach.png` where marked. Publish 1–2 days *after* the Show HN + short LinkedIn post, so it amplifies launch momentum rather than splitting it.

---

**(Hero image: dashboard screenshot)**

A few months ago, I noticed a habit I'd formed without thinking. My Oura app is superb at measurement — every morning it hands me a readiness score — but it doesn't coach. It never answers the question I actually have: *should I train hard today, or recover?* So I'd take my scores and biometrics to a cloud AI chatbot and ask.

The answers were genuinely useful. And then one morning the absurdity of it stopped me cold: to get advice about my body's most intimate signals — nightly heart-rate variability, resting heart rate, core temperature trends, sleep architecture — I was voluntarily shipping that entire history to someone else's server, someone else's model, someone else's terms of service.

I'm an engineer. So instead of just feeling uneasy about it, I built the alternative.

Today I'm open-sourcing **Noctua** 🦉 — an on-device AI wellness toolkit for Oura Ring, written in Kotlin for Android. Every insight it produces is computed locally on the phone. The raw biometric data never leaves the device. Not "encrypted in transit." Not "we take privacy seriously." *Never transmitted, because it never needs to be.*

**Repo: https://github.com/RanjithRagavan/Noctua**

## The architecture decision that matters most

The conventional architecture for "AI health companion" apps looks like this: wearable → phone → vendor cloud → foundation model API → advice. It works, and it's why so many products are built that way. But it quietly makes your endocrine system's nightly readout a third party's asset.

Noctua inverts it:

**Ring → Oura Cloud API → phone → on-device inference → advice. Full stop.**

Three Gradle modules make this concrete:

**`noctua-core`** is a complete, typed Kotlin client for the Oura API v2. Coroutine-first, OAuth2 with automatic token refresh, every `usercollection` endpoint — daily sleep, readiness, activity, SpO2, stress, resilience, detailed sleep periods with embedded HRV streams, workouts, sessions, tags. Pagination via Oura's `next_token` is handled transparently, and HTTP failures surface as typed exceptions instead of status-code archaeology. It's deliberately pure JVM, so the same client runs on Android and on any backend.

**`noctua-ai`** is where the interesting engineering lives. It distills three weeks of ring data into a small set of normalized signals — cumulative sleep debt against your target, last night's HRV expressed as a z-score against *your own* 14-night baseline, the least-squares trend of your readiness scores, temperature deviation. Two engines consume them: an explainable heuristic engine (every insight literally names the feature that triggered it), and a neural readiness forecaster that predicts tomorrow's score via **ExecuTorch**, PyTorch's on-device runtime.

Two details I'm particularly happy with. First, the example app **bundles the ExecuTorch runtime (`org.pytorch:executorch-android:1.4.0`) and the pre-exported `readiness_forecaster.pte` out of the box** — clone and build, and the neural forecaster is already running, with a "neural · ExecuTorch" badge on the forecast card so you can see exactly which engine produced the prediction. Second, inside the library the runtime is bridged reflectively: apps that ship ExecuTorch get neural inference; apps that don't silently fall back to a transparent linear model. The library compiles, tests, and runs either way — dependency optionality without dependency hell.

**`example-app`** is a Jetpack Compose (Material 3) application with score rings, a 14-day readiness trend chart, a forecast card, and the AI coach feed. It boots into a demo mode driven by a synthetic 21-day dataset — no ring, no account, no token — so anyone can evaluate the UX in two minutes:

```bash
git clone https://github.com/RanjithRagavan/Noctua
cd Noctua
./gradlew :example-app:installDebug
```

## What "explainable on-device insight" actually looks like

Here's the entire analysis API:

```kotlin
val oura = OuraClient.Builder().token(token).build()

val report = NoctuaAI().analyze(
    WellnessSnapshot(
        readiness = oura.dailyReadiness(start, end),
        sleep = oura.dailySleep(start, end),
        activity = oura.dailyActivity(start, end),
        sleepPeriods = oura.sleep(start, end),
    )
)

println(report.forecastedReadiness)   // 74 — tomorrow's predicted score
report.insights.forEach { println(it.title) }
// • Sleep debt accumulating (88% confidence)
// • HRV below your baseline (80%)
```

**(Inline image: ai_coach.png — the AI Coach feed)**

And a rule from the heuristic engine, so you can see there's no magic:

```kotlin
when {
    z <= -1.5f -> Insight(STRAIN, WARNING, "HRV below your baseline", ...)
    z >=  1.5f -> Insight(RECOVERY, POSITIVE, "HRV rebound", ...)
}
```

That z-score is last night's average HRV measured in standard deviations against *your* baseline — not a population average. A night 1.5σ below your own norm is a far earlier strain signal than any generic threshold, and it's the kind of personalization that used to require a server. It doesn't anymore.

## The neural part is real, but honest

The readiness forecaster is a tiny MLP — 8 inputs, two hidden layers, one output. It runs in single-digit milliseconds and sips memory. The export path is in the repo (`model/export_readiness_forecaster.py`): `torch.export → to_edge → .pte`, with a synthetic warm-start so the artifact works end-to-end out of the box.

I'll be transparent about what it is and isn't: the shipped weights encode a sensible physiological prior, not a clinically validated model. The point of the release is the *architecture* — a working, testable, privacy-preserving pipeline that you can retrain on a user's own history without changing the `float32 [1,8] → [1,1]` contract. Personalization as a local file, not a cloud subscription. If you want to trace exactly how a night's data becomes a number on screen — every formula and every layer of the .pte execution stack — I documented it in the repo: `docs/HOW_THE_AI_WORKS.md`.

## Why I think this matters beyond one project

On-device ML runtimes — ExecuTorch on Android, Core ML on iOS — have quietly crossed the threshold where the privacy-respecting architecture is also the *faster, cheaper, offline-capable* one. The trade-offs that once justified routing health data through cloud inference are evaporating. What remains is habit.

Health data deserves a different default: **local first, shared only with explicit and revocable consent.** Noctua is my proof-of-craft for that principle — 16 passing unit tests, a real APK, real screenshots, MIT-licensed, issues and PRs open.

If you work in wearables, digital health, or on-device ML, I'd genuinely value your scrutiny. The heuristics in `HeuristicInsightEngine` are deliberately readable — if you have better evidence for a threshold, that's a perfect first contribution.

And if you're an Oura wearer who just wants to see your own data through an explainable, offline lens: the Connect tab takes a Personal Access Token or OAuth2 sign-in, and your morning question — *train or recover?* — gets answered without your body ever leaving your pocket.

**Repo: https://github.com/RanjithRagavan/Noctua**

#AndroidDev #Kotlin #OnDeviceAI #ExecuTorch #DigitalHealth #OpenSource #Privacy #Wearables

---

*Note: Noctua is an independent open-source project, not affiliated with or endorsed by Ōura Health Oy.*
