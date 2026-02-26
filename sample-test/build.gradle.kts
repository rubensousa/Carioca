plugins {
    id("com.android.test")
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    // Not needed for real projects, just here, because we build the plugin locally:
    // https://github.com/gradle/gradle/issues/20084#issuecomment-1060822638
    id(libs.plugins.carioca.allure.get().pluginId)
}

android {
    namespace = "com.rubensousa.carioca.sample.test"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36
        testInstrumentationRunner = "com.rubensousa.carioca.hilt.runner.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    targetProjectPath = ":sample-compose"

    //    Uncomment to check support for flavors
    /* flavorDimensions += "version"
     flavorDimensions += "store"

     productFlavors {
         create("demo") {
             dimension = "version"
         }
         create("full") {
             dimension = "version"
         }
         create("google") {
             dimension = "store"
         }
         create("amazon") {
             dimension = "store"
         }
     }*/
}

dependencies {
    implementation(project(":sample-compose"))
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    debugImplementation(project(":carioca-hilt:carioca-hilt-manifest"))
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.bundles.test.unit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt.android.testing)
    implementation(project(":carioca-junit4-rules"))
    implementation(project(":carioca-hilt:carioca-hilt-fragment"))
    implementation(project(":carioca-hilt:carioca-hilt-compose"))
    implementation(project(":carioca-hilt:carioca-hilt-runner"))
    implementation(project(":carioca-report:report-android"))
    implementation(project(":carioca-report:report-android-compose"))
    androidTestUtil(libs.androidx.test.services)
    androidTestUtil(libs.androidx.test.orchestrator)
}