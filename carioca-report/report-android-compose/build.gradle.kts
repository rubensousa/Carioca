plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kover)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.kotlin.compose)
}

version = parent!!.properties["VERSION_NAME"] as String

android {
    namespace = "com.rubensousa.carioca.android.report.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        targetSdk = 34
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":carioca-report:report-android"))
    api(libs.androidx.ui.test.junit4)
    api(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.material3.android)
}
