# Hacker News — Show HN submission package

> **STATUS (Sep 2, 2026): Show HN temporarily restricted on this account.**
> HN returned: *"We're temporarily restricting Show HNs because of a massive
> influx… Take some time to get to know the community, become a good
> contributor, and then it will be fine to post an occasional Show HN."*
> This is the standard new/low-activity account gate — not a rejection of the
> project. Regular commenting and link submissions remain open. Follow the
> **Account warm-up plan** below, then re-submit this package unchanged.
> Meanwhile the rest of the launch proceeds without HN (see updated sequence
> in `PROMOTION_CHANNELS.md`).

Hacker News (https://news.ycombinator.com) doesn't host articles — you submit
a link, and the "article" is your project page plus your first comment. For an
open-source project like Noctua, the correct format is **Show HN** linking
directly to the GitHub repo. This file is the ready-to-paste package.

---

## Account warm-up plan (do this for 3–4 weeks, then re-submit)

The restriction lifts once the account looks like a genuine community member.
There is no published karma threshold — the signal is *consistent, real
participation*. Do this:

| Week | Action |
|---|---|
| 1–2 | Comment on 2–3 front-page threads per week — technical substance only (Android, on-device ML, wearables, privacy). No links to your own stuff. Aim for comments you'd be proud to have quoted |
| 1–3 | Submit 1–2 *interesting non-self* links per week (good technical articles you actually read) — submitters with a history of quality links get trusted faster |
| 2–4 | Reply thoughtfully to comments on your comments; upvote good stories; use the site normally |
| 4 | Re-submit this Show HN package (Tue–Thu, 8–10 AM ET). If still restricted, wait 1–2 more weeks and repeat |

Hard rules during warm-up: **no self-links, no asking anyone to upvote or
submit on your behalf, no sockpuppets** — ring detection is aggressive and a
flagged account may never recover. A friend's established account submitting
"Show HN" for you is also against the spirit (Show HN is for creators) —
patience is the only reliable path.

Upside of the delay: by launch time the repo will already have stars, published
articles (ProAndroidDev/HackerNoon), and possibly newsletter mentions — Show HN
threads perform *better* when the project visibly has traction.

---

## Submission (ready to paste once the gate lifts)

**URL:** https://github.com/RanjithRagavan/Noctua

**Title (≤ 80 chars, no emoji, no clickbait):**

```
Show HN: Noctua – On-device AI wellness insights for Oura Ring (Kotlin + ExecuTorch)
```

Alternates, depending on the angle you want the thread to take:

```
Show HN: Noctua – A privacy-first Oura Ring SDK with on-device ML, no cloud
```
```
Show HN: I built an on-device AI coach for my Oura Ring (ExecuTorch, Android)
```

Pick the first for launch; keep the others for a possible re-submit months
later (HN allows occasional reposts if a submission got no traction).

## First comment (post immediately after submitting)

HN culture rewards a candid technical first comment from the creator. Paste
this as the first comment on your own submission:

---

Creator here. I built Noctua after noticing a gap: my Oura app measures
brilliantly but doesn't coach — it tells me my readiness score every morning,
never what to do about it ("train hard today, or recover?"). I was filling
that gap by pasting my biometrics into a cloud chatbot — uploading my most
sensitive health history to answer a question a phone can answer locally.

The repo is three Gradle modules:

- `noctua-core`: typed, coroutine-first Kotlin client for the full Oura API v2
  (OAuth2 client-side + code flow, auto-refreshing tokens, every
  usercollection endpoint, pagination, typed errors). Pure JVM — works on
  Android and backends.
- `noctua-ai`: the on-device part. Feature extraction (sleep debt, HRV z-score
  vs your own 14-night baseline, readiness trend) feeds two engines — an
  explainable heuristic rules engine, and a neural readiness forecaster
  running on ExecuTorch. The ExecuTorch runtime is bridged reflectively, so
  apps without it fall back to a transparent linear model; nothing breaks.
- `example-app`: Compose/Material 3, boots into demo mode with a synthetic
  21-day dataset so you can try it without owning a ring. It already bundles
  `executorch-android:1.4.0` and the pre-exported `readiness_forecaster.pte`,
  so the neural forecaster runs on first launch — the forecast card shows a
  "neural · ExecuTorch" badge when the .pte produced the prediction.

The model is deliberately tiny (8→32→16→1 MLP, ~6 KB .pte — the base model is
defined/warm-started in PyTorch in `model/export_readiness_forecaster.py`,
then exported via torch.export → to_edge → to_executorch). Contract is
float32 [1,8] → [1,1], so you can retrain on your own history and swap the
.pte without touching app code. I also wrote a full trace of one data point
from Oura JSON to on-screen forecast — every formula and every layer of the
.pte stack — in docs/HOW_THE_AI_WORKS.md.

Design constraints I held to: raw biometrics never leave the device, every
insight must be traceable to the features that triggered it (no LLM vibes),
and the whole analysis runs in milliseconds offline.

Happy to answer questions about the Oura API quirks (next_token pagination,
sandbox mirror, scope gotchas), the ExecuTorch export path (torch.export →
to_edge → .pte), or why I chose z-score-of-personal-baseline over population
thresholds for HRV.

---

## Timing & mechanics

- **When:** Tuesday–Thursday, 8–10 AM US Eastern — peak HN front-page
  rotation for technical audiences.
- **Where:** https://news.ycombinator.com/submit — your account needs to be in
  good standing; Show HN has no karma minimum, but a brand-new account with
  zero history can trip filters. Comment genuinely on a few threads in the
  days before if your account is new.
- **Rules that matter:**
  - Title must not ask for upvotes, stars, or "feedback please".
  - Don't ask friends to upvote — ring detection kills submissions.
  - Respond to every substantive comment in the first 2–3 hours; engagement
      velocity is what keeps a Show HN on the front page.
- **Screenshot evidence:** the repo README's screenshot table is your visual
  proof — HN readers click through and judge in 10 seconds.

## Follow-up

If the thread gets traction, log it (date, peak rank, comment count) — for the
EB1A file, a front-page Show HN discussion is exactly the kind of third-party
reception evidence worth preserving with screenshots.

If it doesn't: note the learnings, ship v0.2.0 (LLM sleep coach), and
re-submit in 3–6 months with the "I built..." title variant.
