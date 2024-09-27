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
    api(project(":carioca-report:report-runtime"))
    implementation(libs.junit)
    testImplementation(libs.bundles.test.unit)
}

mavenPublishing {
    version = parent!!.properties["VERSION_NAME"] as String
}
