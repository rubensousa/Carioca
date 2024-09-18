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
    repositories {
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
        google()
        mavenCentral()
    }
}

includeBuild("carioca-report") {
    dependencySubstitution {
        substitute(module("com.rubensousa.carioca.report:runtime"))
            .using(project(":runtime"))
        substitute(module("com.rubensousa.carioca.report:serialization"))
            .using(project(":serialization"))
        substitute(module("com.rubensousa.carioca.junit4:report"))
            .using(project(":junit4"))
    }
}
includeBuild("carioca-report-plugin")



rootProject.name = "carioca"

includeBuild(".")
includeBuild("carioca-report-plugin") {
    dependencySubstitution {
        substitute(module("com.rubensousa.carioca.report:serialization"))
            .using(project(":"))
    }
}

include(":carioca-android:report")
include(":carioca-android:report-coroutines")
include(":carioca-android:report-sample")
include(":carioca-junit4:rules")
