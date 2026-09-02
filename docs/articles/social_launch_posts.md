# Launch-day social posts — X/Twitter + LinkedIn

> **REVISED Sep 2, 2026:** the Show HN is delayed ~3–4 weeks (HN temporarily
> restricts Show HN on new/low-activity accounts — see `hackernews_show_hn.md`
> for the warm-up plan). These posts are now the **launch anchor themselves**:
> post them alongside the ProAndroidDev/Medium push, omit the HN cross-link in
> X post 5 for now, and add a "we're on Hacker News" quote-tweet + LinkedIn
> edit when the Show HN eventually lands.

Timed to go out **together, 8–10 AM in your primary timezone** (Tue–Thu).
Post X thread first, LinkedIn 15–30 min later.

Assets: attach `docs/screenshots/dashboard.png` (score rings + forecast) to
the first X post and the LinkedIn post. `ai_coach.png` works for reply posts.

---

## X / Twitter — 5-post thread

**Post 1** *(attach dashboard.png)*

```
I kept pasting my Oura readiness score into a cloud chatbot every morning.

Then it hit me: I was uploading my most sensitive biometric data to answer a question a phone can answer locally.

So I built the alternative. Meet Noctua 🦉 — on-device AI wellness insights for Oura Ring, open source:

https://github.com/RanjithRagavan/Noctua
```

**Post 2**

```
What it does:

• Typed Kotlin client for the full Oura API v2 (OAuth2, every endpoint)
• Computes sleep debt, HRV deviation vs YOUR baseline, readiness trend
• Predicts tomorrow's readiness via ExecuTorch — and the example app SHIPS the runtime + pre-exported .pte, so it runs on first launch
• The forecast card shows which engine answered: "neural · ExecuTorch" vs linear fallback
• Every insight is explainable. No LLM vibes.
```

**Post 3**

```
The rule I held myself to: raw biometrics NEVER leave the device.

No cloud round-trips. No third-party processors. The whole analysis runs in milliseconds, in airplane mode.

Health data should default to local. On-device runtimes finally make that the convenient choice, not the ascetic one.
```

**Post 4** *(attach ai_coach.png)*

```
The demo mode needs no ring and no account — a synthetic 21-day dataset drives the full UX: score rings, forecast card, AI coach feed.

Clone → ./gradlew :example-app:installDebug → running in 2 minutes.
```

**Post 5** *(launch version — no HN yet)*

```
Full write-up on Medium (link in reply). Show HN coming once my account clears HN's new-user gate — I'll post the thread here when it lands.

Roadmap: fully local LLM sleep coach (ExecuTorch Llama runner), nightly on-device personalization, Health Connect write-back.

⭐ the repo if this resonates — it's how independent open source gets seen.
```

**Post 5** *(hold this variant for the week-4 Show HN wave)*

```
Noctua is on Show HN today — would love your technical questions there:
[HN thread URL — paste after submitting]

Roadmap: fully local LLM sleep coach (ExecuTorch Llama runner), nightly on-device personalization, Health Connect write-back.

⭐ the repo if this resonates — it's how independent open source gets seen.
```

*(Note: the "Show HN" cross-link goes in the thread, not the HN title —
asking for upvotes on HN itself is against the rules; cross-promoting FROM X
to the HN thread is fine and common.)*

---

## LinkedIn post *(attach dashboard.png)*

> Rephrased Sep 2 with ExecuTorch / on-device AI / wellness emphasis.

```
I just open-sourced something I've been building: Noctua 🦉 — an on-device AI wellness coach for the Oura Ring, powered by ExecuTorch, PyTorch's on-device runtime.

The story: every morning I was screenshotting my readiness score and pasting it into a cloud chatbot to ask "should I train hard today?" It worked well — and then I realized I was uploading my nightly HRV, resting heart rate, and body temperature history to answer a question a modern phone can answer entirely on-device.

So I built the architecture I wanted to exist — where the AI comes to the data, not the other way around:

🧠 Real neural inference on the phone: a readiness forecaster exported from PyTorch to ExecuTorch (.pte) predicts tomorrow's readiness score in milliseconds — offline, in airplane mode. The example app bundles the runtime and pre-exported model, so it works on first launch, and the forecast card shows exactly which engine answered: "neural · ExecuTorch."

📊 Wellness intelligence, not generic chatbot output: sleep-debt accounting, HRV z-scores against your personal 14-night baseline, readiness trend analysis — every insight is explainable and traceable to the biometric feature that triggered it. No LLM vibes, no hallucinated health advice.

🔧 A complete typed Kotlin client for the Oura API v2 (OAuth2 with auto-refresh, every endpoint, transparent pagination) + a Jetpack Compose example app with a demo mode that needs no ring and no account.

The design principle: raw biometrics never leave the device. On-device AI runtimes like ExecuTorch have quietly crossed the threshold where the privacy-respecting architecture is also the faster, offline-capable one — and wellness data deserves exactly that default.

This is my contribution back to the open-source ecosystem that shaped my career.

Repo, docs, the PyTorch→ExecuTorch export script, and a 2-minute setup:
https://github.com/RanjithRagavan/Noctua

I'd genuinely value your feedback — especially from folks working in on-device ML, digital health, or wearables.

#OnDeviceAI #ExecuTorch #PyTorch #AndroidDev #Kotlin #DigitalHealth #Wearables #EdgeAI #OpenSource #Privacy
```

---

## Launch-hour timeline (all US Eastern) — wave 1, no HN

| Time | Action |
|---|---|
| T-1 day | Confirm Medium paywall OFF + featured image renders; finalize X post 1's screenshot |
| 8:00 AM | Post X thread (posts 1–5, launch version) |
| 8:30 AM | Post LinkedIn |
| 8:00–11:00 AM | Reply to X/LinkedIn comments; email ProAndroidDev |
| 6:00 PM | Thank-you reply on X with any fun stat (stars, comments) |
| T+1 day | Log evidence for EB1A: screenshots of engagement, star count, any pickups |

## Week-4 wave — when the Show HN gate lifts

| Time | Action |
|---|---|
| 8:00 AM | Submit Show HN (title + repo URL from `hackernews_show_hn.md`) |
| 8:01 AM | Paste first comment on the HN thread |
| 8:05 AM | Quote-tweet the launch thread with the HN link (held variant of post 5); edit the LinkedIn article to add the HN thread link |
| 8:00 AM–2:00 PM | Answer every HN comment within minutes — engagement velocity keeps a Show HN alive |
| T+1 day | Log evidence: HN rank/points/comments screenshots, star-count jump |
