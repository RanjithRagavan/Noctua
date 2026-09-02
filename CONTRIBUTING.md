# Contributing to Noctua

Thanks for your interest — contributions of all sizes are welcome, from typo
fixes to new insight engines. Noctua is MIT-licensed; by contributing you
agree your work is released under the same license.

## Project layout

| Module | Purpose |
|---|---|
| `noctua-core` | Pure-Kotlin client for the Oura API v2 (Android + JVM) |
| `noctua-ai` | On-device insight engine (heuristics + ExecuTorch forecaster) |
| `example-app` | Jetpack Compose demo app |
| `model/` | PyTorch → ExecuTorch export script |

## Setting up

1. Clone and open in **Android Studio** (Hedgehog or newer) or any
   Gradle-aware IDE.
2. JDK 17+ and the Android SDK (API 35) — `local.properties` needs `sdk.dir`.
3. Sanity check:
   ```bash
   ./gradlew test                          # library unit tests (16+)
   ./gradlew :example-app:assembleDebug    # Compose app APK
   ```

No Oura account is needed for development: the example app has a demo mode,
and `OuraClient.Builder().sandbox(true)` targets Oura's sandbox endpoints.

## Great first contributions

- **Improve a heuristic.** `HeuristicInsightEngine` is deliberately readable.
  If you have better evidence for a threshold (sleep debt, HRV z-score,
  temperature deviation), that's a perfect first PR — cite your source in the
  KDoc.
- **Add an endpoint model.** New Oura v2 collections can follow the existing
  pattern in `model/` + `api/OuraApi.kt` + a passthrough in `OuraClient`.
- **Compose polish.** Charts, theming, accessibility in `example-app`.
- **Docs.** README clarifications, KDoc, article translations.

## Ground rules

- **Privacy is the product.** No analytics, no tracking SDKs, no network
  calls besides `api.ouraring.com`. PRs that exfiltrate data — even
  "harmless" telemetry — will be rejected.
- **Pure functions where possible.** `noctua-ai` engines take data in and
  return results — no I/O, no clock access, no static state. This keeps them
  unit-testable.
- **No client secrets, ever.** Only the OAuth client *ID* may be configured.
- Keep `noctua-core` and `noctua-ai` **Android-free** (pure JVM) — Android
  dependencies belong in `example-app`.

## Pull request checklist

- [ ] `./gradlew test` passes, and new logic has new tests
- [ ] `./gradlew :example-app:assembleDebug` still builds
- [ ] KDoc on new public API
- [ ] No credentials, tokens, or personal data in code, tests, or fixtures
- [ ] PR description explains the *why*, not just the *what*

## Reporting bugs / proposing features

Use the GitHub issue templates. For anything involving real Oura responses,
**redact all personal data** before pasting JSON.

## Code of conduct

Be kind, be technical, assume good intent. Disagreement about thresholds and
architectures is welcome; disrespect is not.
