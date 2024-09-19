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
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.rubensousa.carioca.android.report"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":carioca-junit4:report"))
    api(libs.carioca.report.json)
    api(libs.androidx.junit)
    api(libs.androidx.test.rules)
    api(libs.androidx.espresso.core)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.uiautomator)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.test.unit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestUtil(libs.androidx.test.services)
}

mavenPublishing {
    coordinates(
        groupId = "com.rubensousa.carioca.android",
        artifactId = "report",
        version = libs.versions.cariocaAndroid.get()
    )
    pom {
        name = "Carioca Android Report"
        description = "Library that generates reports for Android instrumented tests"
        packaging = "aar"
        inceptionYear.set("2024")
        url.set("https://github.com/rubensousa/carioca/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("rubensousa")
                name.set("Ruben Sousa")
                url.set("https://github.com/rubensousa/")
            }
        }
        scm {
            url.set("https://github.com/rubensousa/carioca/")
            connection.set("scm:git:git://github.com/rubensousa/carioca.git")
            developerConnection.set("scm:git:ssh://git@github.com/rubensousa/carioca.git")
        }
    }
}
