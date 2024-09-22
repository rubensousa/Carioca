import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(libs.carioca.report.runtime)
    implementation(libs.junit)
    testImplementation(libs.bundles.test.unit)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    coordinates(
        groupId = "com.rubensousa.carioca",
        artifactId = "junit4-report",
        version = project.parent!!.properties["VERSION_JUNIT4_REPORT"] as String
    )
    pom {
        name = "Carioca Junit4 Report"
        description = "Report data structures for junit4 tests"
        packaging = "jar"
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