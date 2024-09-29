/*
 * Copyright 2024 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.carioca.allure)
}

android {
    namespace = "com.rubensousa.carioca.sample.compose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rubensousa.carioca.sample.compose"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.rubensousa.carioca.hilt.runner.HiltTestRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
        testInstrumentationRunnerArguments["listener"] = "com.rubensousa.carioca.report.android.CariocaInstrumentedListener"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    /* testOptions {
         execution = "ANDROIDX_TEST_ORCHESTRATOR"
     }*/

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.hilt.navigation.compose)

    debugImplementation(libs.ui.tooling)
    debugImplementation(project(":carioca-hilt:carioca-hilt-manifest"))


    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.uiautomator)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(libs.dagger.hilt)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.dagger.hilt.compiler)
    androidTestImplementation(project(":carioca-junit4-rules"))
    androidTestImplementation(project(":carioca-hilt:carioca-hilt-fragment"))
    androidTestImplementation(project(":carioca-hilt:carioca-hilt-compose"))
    androidTestImplementation(project(":carioca-hilt:carioca-hilt-runner"))
    androidTestImplementation(project(":carioca-report:report-android"))
    androidTestImplementation(project(":carioca-report:report-android-coroutines"))
    androidTestUtil(libs.androidx.test.services)
    //  androidTestUtil(libs.androidx.test.orchestrator)
}
