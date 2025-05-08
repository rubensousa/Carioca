plugins {
    id("com.android.test")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    // Not needed for real projects, just here, because we build the plugin locally:
    // https://github.com/gradle/gradle/issues/20084#issuecomment-1060822638
    id(libs.plugins.carioca.allure.get().pluginId)
}

android {
    namespace = "com.rubensousa.carioca.sample.test"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.rubensousa.carioca.hilt.runner.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(project(":carioca-report:report-android"))
    implementation(libs.androidx.junit)
    implementation(libs.junit)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    debugImplementation(project(":carioca-hilt:carioca-hilt-manifest"))
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.bundles.test.unit)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.dagger.hilt)
    implementation(libs.dagger.hilt.android.testing)
    implementation(project(":carioca-junit4-rules"))
    implementation(project(":carioca-hilt:carioca-hilt-fragment"))
    implementation(project(":carioca-hilt:carioca-hilt-compose"))
    implementation(project(":carioca-hilt:carioca-hilt-runner"))
    implementation(project(":carioca-report:report-android"))
    implementation(project(":carioca-report:report-android-compose"))
    implementation(project(":carioca-report:report-android-coroutines"))
    androidTestUtil(libs.androidx.test.services)
    androidTestUtil(libs.androidx.test.orchestrator)
}