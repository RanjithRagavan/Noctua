plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":noctua-core"))

    // ExecuTorch is loaded reflectively at runtime when the Android app ships
    // org.pytorch:executorch-android. No hard dependency keeps this module
    // pure-JVM and testable anywhere.
    testImplementation(libs.junit)
}
