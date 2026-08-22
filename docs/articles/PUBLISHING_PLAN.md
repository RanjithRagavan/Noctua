# Noctua — content & publishing plan

Repo: https://github.com/RanjithRagavan/Noctua

## What's drafted

| File | Platform | Angle |
|---|---|---|
| `medium_on_device_ai_oura.md` | Medium | Story-first: why I stopped pasting my ring data into a chatbot; build walkthrough with snippets |
| `hackernoon_privacy_first_health_ai.md` | HackerNoon | Deep technical: API design, feature engineering, reflection bridge, ExecuTorch export |
| `substack_newsletter.md` | Substack | Personal/newsletter voice: the privacy argument + what shipped |
| `hackernews_show_hn.md` | **Hacker News** | Show HN submission package: title, first comment, timing & rules (HN hosts no articles — the repo + first comment *is* the post) |
| `social_launch_posts.md` | X + LinkedIn | Launch-day posts timed to the Show HN hour, with a minute-by-minute timeline |
| `linkedin_article.md` | LinkedIn Article | Long-form (~1,200 words) professional piece — publish 1–2 days after launch to amplify momentum |

(Original ask mentioned “Hackerrank” → confirmed the target is **Hacker News**
— `hackernews_show_hn.md`. The HackerNoon draft stays as an extra venue.)

## Suggested publishing sequence

0. **Hacker News (Show HN)** — Tue–Thu 8–10 AM ET, links straight to the repo.
   Highest-leverage single post; see `hackernews_show_hn.md` for the package.
1. **HackerNoon** — their review cycle adds editorial credibility you can
   cite later.
2. **Medium** 3–4 days later — canonical link back to HackerNoon to avoid SEO
   cannibalization; submit to *Better Programming* or *Android Dev* pubs.
3. **Substack** same week — newsletter to your own list, links to both.
4. Cross-post a 5-tweet/X thread + LinkedIn post with the score-rings screenshot.

## Before publishing — do these once

- [x] Run the example app on a device/emulator, capture screenshots
      (dashboard rings, AI coach feed, connect screen) — **done**, in
      `docs/screenshots/` and embedded in all three articles.
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
