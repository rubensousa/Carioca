dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":allure-gradle-plugin")
project(":allure-gradle-plugin").projectDir = File("../carioca-report/report-android-allure-gradle-plugin")