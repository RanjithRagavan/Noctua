# Noctua — Newsletter & Publication Submission Drafts
**Ready to send. All links verified live as of Aug 23, 2026.**

Repo: https://github.com/RanjithRagavan/Noctua
Medium article: https://medium.com/@iam.ranjith.ragavan/on-device-ai-coach-for-my-oura-ring-475f308fba17
Substack: https://substack.com/home/post/p-212253040
Hacker News: https://news.ycombinator.com/item?id=49397297

---

## 1. Android Weekly — Submit page (androidweekly.net → "Submit")

**What to submit:** link suggestion form. Submit the **repo** this week; submit the **Medium article** next week (don't put both in one issue).

**Link:**
```
https://github.com/RanjithRagavan/Noctua
```

**Suggested title (if the form asks):**
```
Noctua — privacy-first Oura Ring SDK: typed Kotlin client + on-device ExecuTorch AI coach
```

**Short description (if the form asks):**
```
Open-source Android SDK that pairs a complete coroutine-first Kotlin client for the
Oura API v2 (OAuth2, auto-refreshing tokens, every usercollection endpoint) with an
on-device AI layer: feature extraction, explainable rule-based insights, and a neural
readiness forecaster exported to ExecuTorch (.pte) with a zero-dependency fallback.
All inference runs on-device in <5ms — no biometric data ever leaves the phone.
Ships with a Material 3 Compose example app that runs in demo mode (no ring needed).
MIT.
```

**Week 2 submission (article link):**
```
https://medium.com/@iam.ranjith.ragavan/on-device-ai-coach-for-my-oura-ring-475f308fba17
Title: On-Device AI Coach for My Oura Ring — Noctua, a privacy-first Kotlin SDK (ExecuTorch)
```

---

## 2. Kotlin Weekly — Email submission (contact link in kotlinweekly.net footer)

**To:** (use the contact/submission email listed on kotlinweekly.net)
**Subject:** Submission for next issue: Noctua — typed Oura API v2 Kotlin client + on-device ML

```
Hi Kotlin Weekly team,

I'd like to submit Noctua for an upcoming issue:

https://github.com/RanjithRagavan/Noctua

Noctua is an open-source Kotlin toolkit (MIT) that combines:

- noctua-core: a coroutine-first, fully typed client for the Oura API v2 —
  OAuth2 helpers with automatic token refresh, every usercollection endpoint,
  transparent pagination, and typed error mapping. Pure JVM, so it runs on
  Android or any backend.
- noctua-ai: an on-device intelligence layer — feature extraction (sleep debt,
  HRV z-score vs. personal baseline), an explainable heuristic insight engine,
  and a neural readiness forecaster bridged to PyTorch's ExecuTorch runtime,
  with a zero-dependency linear fallback if the runtime is absent.
- A Material 3 Compose example app with a demo mode that needs no Oura account.

The privacy angle: raw biometric data never leaves the device — all insight
generation and forecasting happen locally, offline-capable, in milliseconds.

I also wrote up the build story here:
https://medium.com/@iam.ranjith.ragavan/on-device-ai-coach-for-my-oura-ring-475f308fba17

A Compose Multiplatform / iOS port is on the roadmap, so the project should
become relevant to the KMP side of the community as well.

Thanks for considering it!

RanjithKumar Ragavan
Principal Engineer / Member of Technical Staff, T-Mobile USA
https://github.com/RanjithRagavan
```

---

## 3. ProAndroidDev — Medium publication submission

**Process reminder:** use the "Submit" link on the ProAndroidDev Medium publication page
(https://medium.com/proandroiddev) and send them your story link. Read their Submission
Guidelines first: https://proandroiddev.com/submission-guidelines-b2efa7f46272
Once accepted you are added as a writer and future articles submit directly via
Medium's "Add to publication" menu.

**Story link to submit:**
```
https://medium.com/@iam.ranjith.ragavan/on-device-ai-coach-for-my-oura-ring-475f308fba17
```

**Pitch / cover note (if the form asks why it fits):**
```
On-Device AI Coach for My Oura Ring

A build story and technical walkthrough of Noctua, an open-source Android SDK that
runs the full wellness-insight loop on-device: a typed Oura API v2 Kotlin client,
an explainable heuristic insight engine, and a neural readiness forecaster exported
to PyTorch's ExecuTorch (.pte) and loaded reflectively with a graceful fallback.

Android-relevant content: coroutine-first API client design, OAuth2 client-side flow
with Custom Tabs, ExecuTorch integration on Android (model export, reflective runtime
loading, zero-dependency fallback), and a Material 3 Compose example app with a
demo mode readers can run in three commands.

Fits the publication's Android development focus; code is open source (MIT):
https://github.com/RanjithRagavan/Noctua
```

**Pre-submission checklist (their guidelines):**
- [ ] Article has a clear featured image (dashboard screenshot works)
- [ ] Code snippets formatted as code blocks (already done)
- [ ] No paywall-locked reading if guidelines require member-only off — check current policy
- [ ] Grammar pass — read once aloud before submitting

---

## 4. Bonus: Mobile Dev Weekly (Cooperpress) — same model as Android Weekly

**Submit:** link suggestion form on mobiledevweekly.com (or the "Submit" link in any issue).

**Link + blurb:**
```
https://github.com/RanjithRagavan/Noctua
Noctua: privacy-first on-device AI for Oura Ring wearables — typed Kotlin API client +
ExecuTorch neural forecasting, all inference on-device, Compose demo app included.
```

---

## Submission order (to keep links "fresh" for each curator)

| When | Action |
|---|---|
| Mon Aug 24 | Android Weekly → repo link; Kotlin Weekly email; ProAndroidDev submit |
| Tue Aug 25 | Mobile Dev Weekly → repo link |
| Wed Aug 26 | HackerNoon → submit existing draft (hackernoon_privacy_first_health_ai.md) |
| Week of Aug 31 | Android Weekly → Medium article link (second, separate suggestion) |
| Ongoing | Screenshot every confirmation/acceptance email → save to this workspace |

## Evidence log rule

Every submission gets a row in `Noctua_Launch_Log.csv`:
date | platform | URL submitted | confirmation evidence | status | result URL | metrics snapshot
