import com.rubensousa.carioca.report.android.allure.gradle.AllureReportExtension

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

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.dokka) apply true
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt) apply false
    id(libs.plugins.carioca.allure.get().pluginId) apply false
}


subprojects {
    group = property("GROUP") as String

    /**
     * Move all allure-results to the same directory,
     * so that all tests from the project are seen in a single report
     */
    plugins.withId("com.rubensousa.carioca.report.allure") {
        extensions.getByType(AllureReportExtension::class).apply {
            outputDir = rootProject.file("build/outputs/allure-results")
        }
    }

}
