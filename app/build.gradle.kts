import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
}

/**
 * Reads the current app version from version.md.
 * Looks for the line starting with "Current: " and returns the version string
 * (e.g. "V0.1.4"). Falls back to "V0.0.0" if the file or line is missing.
 */
fun readAppVersion(): String {
    val versionFile = rootProject.file("version.md")
    if (!versionFile.exists()) return "V0.0.0"
    return versionFile.readLines()
        .firstOrNull { it.startsWith("Current:") }
        ?.removePrefix("Current:")
        ?.trim()
        ?: "V0.0.0"
}

android {
    namespace = "sabuj.m.myevents"
    compileSdk = 37

    defaultConfig {
        applicationId = "sabuj.m.myevents"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = readAppVersion()

        // Exposes the version to Kotlin code as BuildConfig.APP_VERSION
        buildConfigField("String", "APP_VERSION", "\"${readAppVersion()}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room schema export directory
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // --- Compose BOM ---
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // --- Core UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)
    implementation(libs.google.material)

    // --- Compose ---
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- Navigation ---
    implementation(libs.androidx.navigation.compose)

    // --- Room ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Hilt ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // --- Hilt + WorkManager ---
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // --- DataStore ---
    implementation(libs.androidx.datastore.preferences)

    // --- WorkManager ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Firebase ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    // --- Security & Biometrics ---
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.biometric)

    // --- Glance (widget) ---
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}