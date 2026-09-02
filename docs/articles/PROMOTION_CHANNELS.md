# Noctua Launch & Promotion Playbook

Master list of every realistic channel for promoting **Noctua** (github.com/RanjithRagavan/Noctua) — what to post, where, when, how it works, the rules that can get you banned, and what a realistic outcome looks like. Organized into three tiers: launch day, week 1–2, and evergreen.

> **Golden rules before anything else**
> 1. **Never buy stars, forks, followers, or upvotes.** It is detectable, it can get accounts and repos banned, and for an EB1A profile purchased metrics are worse than zero metrics — adjudicators and community members treat them as fraud.
> 2. **Only demo-mode screenshots/videos publicly.** Never publish real health data.
> 3. **90/10 rule on Reddit:** roughly 9 genuine contributions for every 1 self-promotional post. Accounts that only promote get banned quickly.
> 4. **Log everything for EB1A evidence:** screenshots of front-page placements, star counts over time, newsletter mentions, and article view counts. Keep a dated folder.

---

## Tier 1 — Launch Day (single coordinated push)

| Channel | What to post | Mechanism | Timing | Rules / gotchas | Realistic outcome | Status |
|---|---|---|---|---|---|---|
| **Hacker News — Show HN** | `Show HN: Noctua – On-device AI coach for your Oura ring (ExecuTorch on Android)` linking to the GitHub repo | Submit at news.ycombinator.com/submit with your own account | Tue–Thu, **8–10 AM US Eastern** — **⚠ DELAYED ~3–4 weeks: account hit HN's new-user Show HN restriction (Sep 2). Follow the warm-up plan in `hackernews_show_hn.md` first** | Title must be factual, no clickbait. First comment from you explaining the tech (ExecuTorch .pte, OAuth, OAuth-free library design). Reply to every comment in the first 6 hours. Do NOT ask friends to upvote — ring voting gets posts killed. New/low-activity accounts are temporarily blocked from Show HN — warm up the account with genuine comments/submissions first | 10–40 points typical for a good Show HN; 100+ = front page = 5–15k repo visits, 100–500 stars | Package ready (`hackernews_show_hn.md`); **blocked until account warm-up completes** |
| **X / Twitter thread** | 6–8 tweet thread: hook → problem → demo GIF → ExecuTorch architecture → "it's MIT, contributions welcome" → repo link | Post from your account | **Now the launch-day anchor** (previously timed to Show HN). Post alongside the ProAndroidDev/Medium push instead; re-post a "we're on HN" quote-tweet when the Show HN eventually lands | Native video/GIF of the app demo outperforms links. Tag @ouraring (low chance of reply, zero cost), use #AndroidDev #ExecuTorch #QuantifiedSelf sparingly | 5k–50k impressions if picked up by Android dev accounts; main value is screenshots for articles later | Draft ready — post 5's HN cross-link now waits for the delayed Show HN |
| **LinkedIn post + article** | Post: short launch note with demo GIF and repo link. Article: the long-form technical story (already drafted) | Post natively on LinkedIn | Same day as the X thread, 8–10 AM in your primary network's timezone | LinkedIn suppresses external links in-body — put the repo link in the first comment too. Ask 2–3 colleagues for early comments (comments weigh more than likes) | 1k–10k views; strong EB1A evidence because it's tied to your real name and professional identity | Article draft ready |

---

## Tier 2 — Week 1–2 (written content & communities)

| Channel | What to post | Mechanism | Timing | Rules / gotchas | Realistic outcome | Status |
|---|---|---|---|---|---|---|
| **Medium + ProAndroidDev** | Your existing Medium post: medium.com/@iam.ranjith.ragavan/on-device-ai-coach-for-my-oura-ring-475f308fba17 | Email **editors@proandroiddev.com** requesting publication inclusion | Week 1, any day | They require: Android-dev topic ✅, featured image ✅, **code snippets >5 lines as GitHub gists**, structured problem→solutions→conclusion, **NOT behind the paywall** — open the story settings and turn metering OFF before emailing (see note below) | If accepted: 5k–50k reads, the single biggest Android-audience boost available | Email drafted; **verify paywall is off first** |
| **HackerNoon** | Adapted version of the technical article | hackernoon.com → sign up → submit story (free) | Week 1 | Editorial review takes 1–5 days. They add canonical links — fine. Must be original-ish framing, not a verbatim copy-paste of Medium | 1k–10k reads, permanent backlink, decent domain authority for your name | Needs adaptation draft |
| **Substack** | Newsletter-format version for subscribers (builder-journal tone: "Week 1 of building an on-device sleep AI") | Your own Substack, cross-post | Week 1–2 | No gatekeeping. Value is building a subscriber asset you own | Small initially; compounds over months | Draft ready |
| **dev.to** | Cross-post the Medium article with `canonical_url` set to the Medium URL | dev.to editor → settings icon → set canonical URL | Week 1 | Canonical URL is mandatory so Google doesn't split SEO credit. Add the demo video/GIF | 500–5k views, good dev-community comments | Not started |
| **Hashnode** | Same cross-post strategy with canonical URL | hashnode.com import | Week 1–2 | Same as dev.to | Similar to dev.to, smaller | Not started |
| **Android Weekly** (androidweekly.net, ~66k subscribers) | Submit the Medium/ProAndroidDev article URL | "Suggest a link" form on androidweekly.net | Week 2 (after ProAndroidDev accepts, so you submit the stronger URL) | Editors hand-pick; no guarantee. A Show HN front page or ProAndroidDev acceptance raises odds a lot | If picked: 3k–10k clicks in a day, big star bump | Pending ProAndroidDev |
| **Kotlin Weekly** (kotlinweekly.net) | Submit article + suggest Noctua for the Libraries section | Submission form / email listed on kotlinweekly.net | Week 2 | Libraries section fits a repo perfectly; article fits their Articles section | If picked: 1k–5k clicks | Not started |
| **r/droidappshowcase** | "Noctua — on-device AI coach for Oura ring, open source" with demo GIF + repo link | Direct post — this is Reddit's designated app-showcase sub | Week 1 | This sub EXISTS for self-promotion (r/androidapps redirects promo here). Still: engaging title, respond to comments | 50–500 upvotes possible; modest traffic but real users | High-priority |
| **r/SideProject** | "I built an on-device AI that coaches me using my Oura ring data (ExecuTorch on Android)" | Direct post | Week 1 | Promo-friendly. Story-first titles perform best ("I built…") | 100–2k upvotes if the story resonates; good traffic | High-priority |
| **r/IMadeThis** | Same angle as r/SideProject | Direct post | Week 1–2 (stagger from r/SideProject by 2–3 days) | Promo-friendly, smaller | 50–500 upvotes | High-priority |
| **r/QuantifiedSelf** | Technical post about on-device inference over wearable data | Direct post | Week 2 | Small but exactly-on-topic community; genuine discussion expected, not drive-by promo | Low traffic, very high-quality feedback | Not verified — read sidebar first |
| **r/ouraring** | Post ONLY as "I built this open-source tool, mods approved" — or skip | **DM the mods first**, then post | Week 2+ | Self-promo rules unknown; brand subs often ban unsolicited app promo. A mod-approved post is gold here because the audience owns rings | If allowed: your single most relevant user pool | **Verify rules/DM mods before posting** |
| **r/androiddev** | Do NOT self-promote. Participate: answer questions, post in the weekly App Feedback thread | Weekly megathreads only | Ongoing | Self-promotion is explicitly prohibited outside weekly threads; violations = ban | Indirect: builds the karma and history you need for the 90/10 rule | Ongoing habit |
| **r/startups / r/IndieBiz / r/AlphaAndBetaUsers** | r/IndieBiz + r/AlphaAndBetaUsers: direct posts OK. r/startups: weekly "Share Your Startup" thread only | Mixed | Week 2 | r/startups bans direct promo posts | Modest traffic; AlphaAndBetaUsers can give real testers | Optional |

---

## Tier 3 — Evergreen (compounding, weeks 2–8+)

| Channel | What to do | Mechanism | Timing | Rules / gotchas | Realistic outcome |
|---|---|---|---|---|---|
| **awesome-android / awesome-kotlin lists** | PR adding Noctua under Health/Wearables | Fork → add entry in their exact format → PR | Week 3+ (after repo has some stars; maintainers reject empty projects) | PRs can sit for weeks; follow each repo's CONTRIBUTING format exactly. Note: matteocrippa's awesome-kotlin is archived — target actively-maintained lists | Permanent trickle of targeted traffic; also an EB1A "adoption by community" artifact |
| **Product Hunt** | Full launch: tagline "On-device AI coach for your Oura ring", demo video <90s, gallery of screenshots, maker first-comment | producthunt.com/posts/new | **Only after v0.2 is polished.** Launch goes live **12:01 AM PT**; Tue–Thu = max traffic + max competition; weekends are quieter and fine for dev tools | First ~4 hours of votes are hidden; the algorithm discounts new/inactive accounts and coordinated voting — so warm up your PH account NOW (upvote/comment daily for 2 weeks). Recruit 3–5 genuine maker friends to hunt/support, activate your network in waves through the day | Top-5 of the day = 2k–10k visits; a PH badge on the README is a durable credibility asset |
| **F-Droid** | Submit the example app for inclusion | RFP process on fdroid.org (GitLab issue) | When the app is stable | Requires fully FOSS build (no proprietary deps) — check ExecuTorch + OkHttp licensing path; this is real work | Prestige + installs from the FOSS community; strong open-source-credibility signal |
| **Quantified Self forum** (forum.quantifiedself.com) | Show-and-tell post about the project | Forum post | Week 3+ | Community values honest self-experimentation write-ups | Small, but this community's blog posts get cited — good for organic backlinks |
| **YouTube / short demo video** | 2–3 min screen recording: OAuth → dashboard → AI forecast, with voiceover | Upload, link from README + articles | Week 2–3 | Vertical clip doubles as the Product Hunt / X video | Videos embedded in the README raise conversion to stars noticeably |
| **Kotlin Slack + androiddev.social (Mastodon)** | Share in #android / relevant channels with a humble "built this, feedback welcome" | Join kotlinlang.slack.com; post on androiddev.social | Week 1–2 | Slack promo etiquette varies by channel — read channel topics first | Small but influential audience; maintainers and newsletter editors hang out here |
| **DZone / Kodeco / AndroidSweets / OnCreate Dispatch** | Submit article or link via each site's submission form | Forms on each site | Week 3+ | Lower priority; treat as bonus backlinks | Occasional pickups, mostly SEO value |
| **GitHub itself (free, do immediately)** | Topics (`oura`, `executorch`, `android`, `kotlin`, `compose`, `on-device-ai`, `quantified-self`), Discussions enabled, 2–3 `good first issue` labels, CONTRIBUTING.md, a roadmap issue | Repo settings | Now | Topics are how people actually find niche repos | Highest ROI per minute of anything on this list |
| **Lobsters** | Skip self-posting | Invite-only; self-promo is culturally rejected | — | If a member shares your HN post organically, great. Don't force it | — |

---

## 4-week calendar (revised Sep 2 — HN delayed by account restriction)

| Day | Action |
|---|---|
| **Day 0 (prep, before launch)** | GitHub topics + good-first-issues + CONTRIBUTING; warm up Product Hunt account; **start HN account warm-up** (2–3 genuine comments/week on front-page threads + 1–2 non-self link submissions — see `hackernews_show_hn.md`); verify Medium paywall OFF; convert long code blocks in the Medium post to gists; record the demo video |
| **Day 1 (launch, no HN)** | 8–10 AM ET: X thread + LinkedIn post, same hour (post 5's HN cross-link omitted for now). Email ProAndroidDev |
| **Day 2–3** | Post r/droidappshowcase; submit HackerNoon draft |
| **Day 4–7** | r/SideProject; cross-post dev.to (canonical set); publish Substack issue; post in Kotlin Slack |
| **Week 2** | r/IMadeThis; Hashnode cross-post; DM r/ouraring mods; submit Android Weekly + Kotlin Weekly links; upload YouTube demo; QS forum post. **Continue HN warm-up** |
| **Week 3** | awesome-list PRs; r/QuantifiedSelf; keep answering r/androiddev weekly threads. **Continue HN warm-up** |
| **Week 4 (second wave)** | **Submit the Show HN** (Tue–Thu 8–10 AM ET) — the repo now has stars, published articles, and newsletter mentions, so the thread starts stronger. Quote-tweet it on X, add the HN link to the LinkedIn article as an edit. Schedule Product Hunt for a Tue–Thu once v0.2 is ready |

---

## EB1A evidence note

Every placement above is a potential exhibit. Keep a dated evidence folder with:

- Screenshots of the HN post with point count, ProAndroidDev publication page, newsletter features
- StarHistory chart snapshots at milestones (100 / 500 / 1000 stars)
- Download/clone stats from GitHub Insights (only visible to you for 14 days — screenshot weekly!)
- Article view counts from Medium/dev.to/HackerNoon dashboards
- Any third-party mention (someone else's tweet, blog roundup, awesome-list merge)

Organic, third-party validation (newsletter editors choosing your link, strangers opening issues) weighs far more than raw counts.

---

## Medium paywall check (how to turn metering off)

1. Open your story on Medium → click the **⋯ menu** → **Story settings** (or **Edit story → ⋯ → Settings**).
2. Find **"Meter your story"** / **"This story is part of the paywall"** checkbox.
3. **Uncheck it** and save. The story becomes free for everyone — this is exactly what ProAndroidDev requires.
