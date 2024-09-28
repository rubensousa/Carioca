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
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)

}

version = parent!!.properties["VERSION_NAME"] as String

android {
    namespace = "com.rubensousa.carioca.hilt.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "com.rubensousa.carioca.hilt.runner.HiltTestRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        targetSdk = 34
    }
}

dependencies {
    api(project(":carioca-hilt:carioca-hilt-manifest"))
    api(libs.androidx.ui.test.junit4)
    api(libs.androidx.ui.test.manifest)
    api(libs.androidx.test.core)
    api(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    androidTestImplementation(project(":carioca-hilt:carioca-hilt-runner"))
    androidTestImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.androidx.lifecycle.viewmodel)
    androidTestImplementation(libs.androidx.lifecycle.viewmodel.compose)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.dagger.hilt.compiler)
    androidTestUtil(libs.androidx.test.services)
}
