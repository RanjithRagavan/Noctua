import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Optional local configuration (local.properties is git-ignored):
//   OURA_CLIENT_ID=<your Oura OAuth application client ID>
// Only the client *ID* belongs here — never put the client SECRET in an app;
// it could be extracted from the APK. The client-side OAuth flow needs no secret.
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val ouraClientId = localProps.getProperty("OURA_CLIENT_ID", "").orEmpty()

android {
    namespace = "com.noctua.example"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.noctua.example"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        buildConfigField("String", "OURA_CLIENT_ID", "\"$ouraClientId\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":noctua-core"))
    implementation(project(":noctua-ai"))

    // Optional: ship the ExecuTorch runtime to enable the neural forecaster.
    // implementation("org.pytorch:executorch-android:1.0.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)

    debugImplementation(libs.compose.ui.tooling)
}
