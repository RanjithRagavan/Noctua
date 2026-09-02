# Noctua — content & publishing plan

Repo: https://github.com/RanjithRagavan/Noctua

## What's drafted

| File | Platform | Angle |
|---|---|---|
| `medium_on_device_ai_oura.md` | Medium | Story-first: why I stopped pasting my ring data into a chatbot; build walkthrough with snippets; expanded ExecuTorch section |
| `hackernoon_privacy_first_health_ai.md` | HackerNoon | Deep technical: API design, feature engineering, reflection bridge, ExecuTorch export |
| `substack_newsletter.md` | Substack | Personal/newsletter voice: the privacy argument + what shipped |
| `hackernews_show_hn.md` | **Hacker News** | Show HN submission package: title, first comment, timing & rules (HN hosts no articles — the repo + first comment *is* the post) |
| `social_launch_posts.md` | X + LinkedIn | Launch-day posts timed to the Show HN hour, with a minute-by-minute timeline |
| `linkedin_article.md` | LinkedIn Article | Long-form (~1,200 words) professional piece — publish 1–2 days after launch to amplify momentum |
| `newsletter_submissions.md` | Android/Kotlin Weekly, ProAndroidDev, Mobile Dev Weekly | Ready-to-send newsletter submission blurbs + order |
| `proandroiddev_submission_email.md` | ProAndroidDev editors | Submission email for the Medium post |
| `PROMOTION_CHANNELS.md` | Master playbook | Tiered channel tables, rules, realistic outcomes, 3-week calendar |

(Original ask mentioned “Hackerrank” → confirmed the target is **Hacker News**
— `hackernews_show_hn.md`. The HackerNoon draft stays as an extra venue.)

## Suggested publishing sequence

> **Revised Sep 2, 2026:** HN temporarily restricted Show HN on the account
> (new-account gate). Sequence flipped: articles + social first, Show HN after
> a 3–4 week account warm-up (`hackernews_show_hn.md`).

0. **Medium + ProAndroidDev email + X thread + LinkedIn** — the launch anchor.
   Update the Medium post with the latest draft first (bundled ExecuTorch
   runtime, "neural · ExecuTorch" badge, HOW_THE_AI_WORKS deep dive).
1. **HackerNoon** — their review cycle adds editorial credibility you can
   cite later.
2. **Substack** same week — newsletter to your own list, links to both.
3. **Reddit** (r/droidappshowcase, r/SideProject, r/IMadeThis) — staggered
   over the first two weeks.
4. **Hacker News (Show HN)** — week 4, after the warm-up plan. By then the
   repo has stars, published articles, and newsletter mentions, so the thread
   starts stronger. Tue–Thu 8–10 AM ET, links straight to the repo;
   see `hackernews_show_hn.md` for the package.

## Before publishing — do these once

- [x] Run the example app on a device/emulator, capture screenshots
      (dashboard rings, AI coach feed, connect screen) — **done**, in
      `docs/screenshots/` and embedded in all three articles.
- [x] Connect a real Oura account via OAuth — **done** on emulator
      ("Connected: 9 readiness, 9 sleep, 11 activity, 13 periods"); the live
      pipeline works end-to-end. Decision: keep **demo-mode screenshots**
      for all public articles (never publish real health data).
- [x] ExecuTorch end-to-end — **done**: example app bundles
      `executorch-android:1.4.0` + pre-exported `readiness_forecaster.pte`,
      forecast card shows "neural · ExecuTorch". All article drafts updated
      to highlight this (Sep 1 refresh).
- [x] Deep-dive doc — **done**: `docs/HOW_THE_AI_WORKS.md` (formulas +
      .pte execution stack); now referenced from Medium, Substack, HN first
      comment, and LinkedIn drafts.
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
