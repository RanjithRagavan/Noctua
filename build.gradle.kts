// Noctua — root build configuration.
// Modules:
//   noctua-core  : pure-Kotlin typed client for the Oura API v2 (Android + JVM)
//   noctua-ai    : privacy-first on-device insight engine (heuristics + ExecuTorch)
//   example-app  : Jetpack Compose companion showcasing the libraries
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
