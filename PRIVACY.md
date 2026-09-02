# Privacy Policy — Noctua

**Last updated: September 1, 2026**

Noctua is an open-source Android application and SDK that connects to your
Oura Ring account through the official Oura API v2 and generates wellness
insights **entirely on your device**. This policy describes, plainly, what
happens to your data.

## The short version

**Your biometric data is processed on your phone and is never transmitted to
any server operated by the developer or by any third party. There is no
analytics, no tracking, no advertising, and no account system.**

## What data the app accesses

With your explicit authorization via Oura's OAuth2 consent screen, the app
reads the categories you approve from the Oura API v2, which may include:
daily sleep, readiness, activity, SpO2, stress, resilience and heart-health
summaries, detailed sleep periods (including heart-rate and HRV series),
workouts, sessions, tags, ring configuration, and basic profile information.

## How that data is used

- Data is fetched from `api.ouraring.com` over TLS directly to your device.
- All analysis (feature extraction, insight generation, readiness forecasting)
  runs **on-device**. Raw biometric history is never uploaded anywhere.
- Data is held in app memory/local storage on your device only, and is
  removed when you uninstall the app or clear its data.

## Credentials

OAuth2 access tokens are stored locally on your device and are used solely to
authenticate requests to the Oura API on your behalf. They are never sent to
any other destination. You can revoke access at any time from your Oura
account at https://cloud.ouraring.com, which immediately disables the app's
access.

## Third parties

The app communicates with exactly one external service: **Oura**
(api.ouraring.com / cloud.ouraring.com), governed by Oura's own privacy
policy. No other third-party SDKs, analytics providers, crash-reporting
services, or advertising networks are included.

## Children's privacy

The app is not directed at children and does not knowingly collect data from
anyone under 13.

## Changes

Changes to this policy are committed publicly to this repository; the history
is auditable in git.

## Contact

RanjithKumar Ragavan — open an issue at
https://github.com/RanjithRagavan/Noctua/issues or contact via the email
registered with the Oura developer application.
