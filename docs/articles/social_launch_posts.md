# Launch-day social posts — X/Twitter + LinkedIn

Timed to go out **the same hour as the Show HN submission** (Tue–Thu,
8–10 AM US Eastern). Post X thread first, LinkedIn 15–30 min later.

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

**Post 5**

```
Also on Show HN today — would love your technical questions there:
[HN thread URL — paste after submitting]

Roadmap: fully local LLM sleep coach (ExecuTorch Llama runner), nightly on-device personalization, Health Connect write-back.

⭐ the repo if this resonates — it's how independent open source gets seen.
```

*(Note: the "Show HN" cross-link goes in the thread, not the HN title —
asking for upvotes on HN itself is against the rules; cross-promoting FROM X
to the HN thread is fine and common.)*

---

## LinkedIn post *(attach dashboard.png)*

```
I just open-sourced something I've been building: Noctua 🦉 — on-device AI wellness intelligence for the Oura Ring.

The story: every morning I was screenshotting my readiness score and pasting it into a cloud chatbot to ask "should I train hard today?" It worked well — and then I realized I was voluntarily uploading my nightly HRV, resting heart rate, and body temperature history to answer a question a modern phone can answer entirely on-device.

So I built the architecture I wanted to exist:

→ A typed, coroutine-first Kotlin client for the complete Oura API v2 (OAuth2 with auto-refresh, every usercollection endpoint, transparent pagination)

→ An on-device AI layer: sleep-debt accounting, HRV z-scores against your personal 14-night baseline, readiness trend analysis — feeding explainable heuristic insights plus a neural readiness forecaster running on ExecuTorch, PyTorch's mobile runtime

→ A Jetpack Compose example app (Material 3, score rings, trend charts, AI coach feed) with a demo mode that needs no ring and no account

The design principle: raw biometrics never leave the device. Every insight is traceable to the data that triggered it. Everything runs offline in milliseconds.

This is my contribution back to the open-source ecosystem that shaped my career — and a small proof that privacy-first health AI is no longer a compromise; it's the better architecture.

Repo, docs, and a 2-minute setup: https://github.com/RanjithRagavan/Noctua

I'd genuinely value your feedback — especially from folks working in wearables, digital health, or on-device ML.

#AndroidDev #Kotlin #OnDeviceAI #ExecuTorch #DigitalHealth #OpenSource #Privacy #Wearables #JetpackCompose
```

---

## Launch-hour timeline (all US Eastern)

| Time | Action |
|---|---|
| T-1 day | Warm up HN account (a few genuine comments); schedule nothing — HN requires manual submission |
| 8:00 AM | Submit Show HN (title + repo URL from `hackernews_show_hn.md`) |
| 8:01 AM | Paste first comment on the HN thread |
| 8:05 AM | Post X thread (posts 1–5), with HN link added to post 5 |
| 8:30 AM | Post LinkedIn |
| 8:00–11:00 AM | Answer every HN comment within minutes; reply to X/LinkedIn comments |
| 6:00 PM | Thank-you reply on X with any fun stat (stars, comments) |
| T+1 day | Log evidence for EB1A: screenshots of HN rank/comments, star count, any press pickups |
