plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(project(":carioca-report:report-runtime"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.bundles.test.unit)
}
