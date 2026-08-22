# Article 3 — Substack (newsletter voice)

**Working title:** *Your ring already knows you're getting sick. Why does the cloud need to?*

---

There's a moment every Oura owner knows: you wake up feeling fine, but the app
says your temperature is up and your HRV tanked — and two days later you're
on the couch with a fever. The ring knew first.

Now here's the uncomfortable version of that story. Most "AI wellness coach"
apps answer your questions by shipping that exact data — nightly HRV, resting
heart rate, body temperature, sleep stages — to a server running someone
else's model. The insight is real, but so is the trade: your body's most
intimate time series becomes somebody else's training-adjacent asset.

This week I shipped the alternative. **Noctua** 🦉 is a small open-source
Android toolkit I've been building that does the whole loop on the phone
itself:

1. It speaks fluent **Oura API v2** — a typed Kotlin client with OAuth2,
   auto-refreshing tokens, and every endpoint from daily readiness to
   minute-level heart rate.
2. It computes the signals that actually matter — sleep debt against *your*
   target, HRV deviation against *your* baseline, readiness trend — not
   population averages.
3. It predicts tomorrow's readiness score with a neural network that runs
   on-device via **ExecuTorch** (PyTorch's mobile runtime), and explains its
   advice with readable rules instead of LLM vibes.
4. It ships with a Compose example app that works in demo mode — no ring, no
   account, no token required — so you can judge the UX in two minutes.

![Noctua's dashboard: readiness, sleep and activity rings with an on-device forecast of tomorrow's readiness](https://raw.githubusercontent.com/RanjithRagavan/Noctua/main/docs/screenshots/dashboard.png)

*This is what "on-device" looks like: score rings, a forecast of tomorrow's readiness, and a coach feed — all computed locally, in airplane mode if you want.*

The design rule was simple: **raw biometrics never leave the device.** Not
because cloud AI is useless, but because health data should default to local
and only travel with explicit, revocable consent. On-device runtimes finally
make that the *convenient* choice, not the ascetic one — the forecast takes
milliseconds and works in airplane mode.

If you build on Android, the README walks from zero to a running app in three
commands. If you wear an Oura and just want to see what your data looks like
through an explainable lens, the demo mode shows exactly that.

→ **https://github.com/RanjithRagavan/Noctua**

Next on the roadmap: a fully local LLM sleep coach (ExecuTorch's Llama
runner), nightly on-device personalization of the forecaster, and Health
Connect integration so Noctua's insights can flow back into Android's health
graph — on your terms.

If this resonates, a star on the repo genuinely helps — it's how independent
open-source work gets seen. And if you build something with it, I'd love to
hear about it.
