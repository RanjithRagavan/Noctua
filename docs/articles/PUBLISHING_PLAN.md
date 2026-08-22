# Noctua — content & publishing plan

Repo: https://github.com/RanjithRagavan/Noctua

## What's drafted

| File | Platform | Angle |
|---|---|---|
| `medium_on_device_ai_oura.md` | Medium | Story-first: why I stopped pasting my ring data into a chatbot; build walkthrough with snippets |
| `hackernoon_privacy_first_health_ai.md` | HackerNoon | Deep technical: API design, feature engineering, reflection bridge, ExecuTorch export |
| `substack_newsletter.md` | Substack | Personal/newsletter voice: the privacy argument + what shipped |

(“Hackerrank” in the original ask is a coding-challenge platform without a
blogging surface — the draft targets HackerNoon, the standard venue for this
kind of technical piece. Say the word if you meant something else.)

## Suggested publishing sequence

1. **HackerNoon first** — their review cycle adds editorial credibility you can
   cite later.
2. **Medium** 3–4 days later — canonical link back to HackerNoon to avoid SEO
   cannibalization; submit to *Better Programming* or *Android Dev* pubs.
3. **Substack** same week — newsletter to your own list, links to both.
4. Cross-post a 5-tweet/X thread + LinkedIn post with the score-rings screenshot.

## Before publishing — do these once

- [ ] Run the example app on a device/emulator, capture 2–3 screenshots
      (dashboard rings, AI coach feed, connect screen), add them to
      `docs/screenshots/` in the repo and embed in each article.
- [ ] If you have an Oura ring: connect a real token and capture one real
      insight card — authentic data beats demo data in screenshots.
- [ ] Add the article URLs back into the repo README once live.
- [ ] Enable GitHub Discussions on the repo (credibility + community surface).

## EB1A positioning notes

- The repo is an **original contribution**: first open-source Android SDK
  combining Oura API v2 with fully on-device ExecuTorch inference.
- Track adoption signals as evidence: stars, forks, dependent projects,
  article view counts, any citations.
- When articles go live, log them in `01_Media_Coverage.csv`-style tracking.
- Consider tagging releases (`v0.1.0` now, `v0.2.0` when the LLM coach lands)
  so contribution history is auditable.
