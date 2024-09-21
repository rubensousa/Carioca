import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

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
    alias(libs.plugins.maven.publish)
}

group = "com.rubensousa.carioca.android"
version = libs.versions.cariocaAllureAndroid.get()

gradlePlugin {
    plugins {
        register("allure") {
            id = "com.rubensousa.carioca.android.allure"
            implementationClass = "com.rubensousa.carioca.android.allure.gradle.AllureReportPlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.carioca.report.json)
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.android)
    implementation(libs.gradle.android.tools)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.bundles.test.unit)
}

mavenPublishing {
    configure(
        GradlePlugin(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )
    coordinates(artifactId = "allure-gradle-plugin")
    pom {
        name = "Carioca Android Allure Report Plugin"
        description = "Plugin that generates instrumented test reports for allure"
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
