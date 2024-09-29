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

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "carioca"

include(":carioca-hilt:carioca-hilt-compose")
include(":carioca-hilt:carioca-hilt-fragment")
include(":carioca-hilt:carioca-hilt-manifest")
include(":carioca-hilt:carioca-hilt-runner")
include(":carioca-junit4-rules")
include(":carioca-report:report-junit4")
include(":carioca-report:report-android")
include(":carioca-report:report-android-allure-gradle-plugin")
include(":carioca-report:report-android-coroutines")
include(":carioca-report:report-runtime")
include(":carioca-report:report-json")
include(":sample-compose")
include(":sample-reports")
