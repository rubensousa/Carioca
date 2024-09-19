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
    `kotlin-dsl`
    id("java-gradle-plugin")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

if (rootProject.name == "carioca") {
    plugins.apply(libs.plugins.maven.publish.get().pluginId)
}

gradlePlugin {
    plugins {
        register("plugin-android-allure") {
            id = "com.rubensousa.carioca.plugin.android.allure"
            implementationClass = "com.rubensousa.carioca.plugin.android.allure.AllureReportPlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.carioca.report.json)
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.android)
    implementation(libs.gradle.android.tools)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.bundles.test.unit)
}
