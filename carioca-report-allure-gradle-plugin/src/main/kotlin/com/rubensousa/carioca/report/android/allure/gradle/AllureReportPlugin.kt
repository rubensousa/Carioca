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

package com.rubensousa.carioca.report.android.allure.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.AndroidTest
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.DynamicFeatureAndroidComponentsExtension
import com.android.build.api.variant.HasAndroidTest
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import com.android.build.api.variant.TestVariant
import com.android.build.api.variant.Variant
import com.rubensousa.carioca.report.json.JsonReportParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

class AllureReportPlugin : Plugin<Project> {

    private val logcatOutputDirPath = "outputs/androidTest-results/connected"
    private val connectedOutputDir = "outputs/connected_android_test_additional_output"
    private val outputDirPath = "outputs/allure-results"
    private val supportedPlugins = listOf(
        "com.android.test",
        "com.android.application",
        "com.android.library",
        "com.android.dynamic-feature"
    )
    private val reportGenerator = AllureReportGenerator(
        logcatFinder = LogcatFileFinder(),
        parser = JsonReportParser(),
    )

    override fun apply(target: Project) {
        target.extensions.add("allureReport", AllureReportExtension::class.java)

        check(supportedPlugins.any { target.plugins.hasPlugin(it) }) {
            "Report generation only works for modules of type: $supportedPlugins"
        }
        val allureExtension = target.extensions.getByType(AllureReportExtension::class.java)
        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)
        when (androidComponents) {
            is TestAndroidComponentsExtension,
            is LibraryAndroidComponentsExtension,
            is ApplicationAndroidComponentsExtension,
            is DynamicFeatureAndroidComponentsExtension,
                -> Unit

            else -> error("${androidComponents.javaClass.name} is not supported")
        }
        registerTasks(target, allureExtension, androidComponents)
    }

    private fun registerTasks(
        project: Project,
        allureExtension: AllureReportExtension?,
        extension: AndroidComponentsExtension<*, *, *>,
    ) {
        extension.onVariants { variant ->
            val testVariant = (variant as? HasAndroidTest)?.androidTest
            val variantName = if (testVariant != null) {
                testVariant.name
            } else if (variant is TestVariant) {
                variant.name
            } else {
                null
            }
            if (variantName != null) {
                registerVariantReportTasks(
                    project = project,
                    allureExtension = allureExtension,
                    variant = variant,
                    testVariant = testVariant,
                    variantName = variantName
                )
            }
        }
    }

    private fun registerVariantReportTasks(
        project: Project,
        allureExtension: AllureReportExtension?,
        variant: Variant,
        testVariant: AndroidTest?,
        variantName: String,
    ) {
        val buildOutputDir = getBuildOutputDir(project)
        val connectedOutputDir = if (testVariant != null) {
            getConnectedOutputDir(project, testVariant)
        } else {
            getConnectedOutputDir(project, variantName)
        }
        connectedOutputDir.mkdirs()

        registerCleanTask(
            project = project,
            variant = variant,
            buildOutputDir = buildOutputDir,
            connectedOutputDir = connectedOutputDir
        )

        val generationTask = registerGenerateTask(
            project = project,
            allureExtension = allureExtension,
            variant = variant,
            connectedOutputDir = connectedOutputDir,
        )

        project.afterEvaluate {
            /**
             * This will ensure that all test tasks will trigger a report generation
             */
            val testTaskName = if (testVariant != null) {
                getTestTaskName(testVariant)
            } else {
                getTestTaskName(variantName)
            }
            val testTask = project.tasks.findByName(testTaskName)
            testTask?.finalizedBy(generationTask)
        }
    }

    private fun getBuildOutputDir(project: Project): File {
        return project.layout.buildDirectory.file(outputDirPath).get().asFile
    }

    private fun registerCleanTask(
        project: Project,
        variant: Variant,
        buildOutputDir: File,
        connectedOutputDir: File,
    ) {
        project.tasks.register("clean${variant.name.capitalized()}AllureReport") {
            it.group = "report"
            it.description = "Deletes the previous generated allure report"
            it.doFirst {
                // Clean-up all the files from the output dirs
                // to avoid conflicts with the next report generation
                buildOutputDir.deleteRecursively()
                connectedOutputDir.deleteRecursively()
            }
        }
    }

    private fun registerGenerateTask(
        project: Project,
        allureExtension: AllureReportExtension?,
        variant: Variant,
        connectedOutputDir: File,
    ): TaskProvider<Task> {
        val logcatOutputDir = getLogcatOutputDir(project, variant)
        val reportOutputDir = allureExtension?.outputDir ?: getBuildOutputDir(project)
        val attachLogcatOnSuccess = allureExtension?.attachLogcatOnSuccess ?: false

        return project.tasks.register("generate${variant.name.capitalized()}AllureReport") {
            it.group = "report"
            it.description = "Generates the allure report for a previous test run"
            it.doLast {
                reportOutputDir.mkdirs()
                reportGenerator.generateReport(
                    testResultDir = connectedOutputDir,
                    logcatOutputDir = logcatOutputDir,
                    outputDir = reportOutputDir,
                    attachLogcatOnSuccess = attachLogcatOnSuccess,
                )
                println("Allure results saved in file:///${reportOutputDir.absolutePath}")
            }
        }
    }

    private fun getTestTaskName(testVariant: AndroidTest): String {
        return "connected${testVariant.name.capitalized()}"
    }

    private fun getTestTaskName(variantName: String): String {
        return "connected${variantName.capitalized()}AndroidTest"
    }

    private fun getConnectedOutputDir(project: Project, testVariant: AndroidTest): File {
        val path = "$connectedOutputDir/${testVariant.name}"
        return project.layout.buildDirectory.file(path).get().asFile
    }

    private fun getConnectedOutputDir(project: Project, variantName: String): File {
        val path = "$connectedOutputDir/${variantName}"
        return project.layout.buildDirectory.file(path).get().asFile
    }

    private fun getLogcatOutputDir(project: Project, variant: Variant): File {
        val flavorName = variant.flavorName
        val buildType = variant.buildType ?: "debug"
        val path = if (flavorName == null) {
            "$logcatOutputDirPath/$buildType"
        } else {
            "$logcatOutputDirPath/$buildType/flavors/$flavorName"
        }
        return project.layout.buildDirectory.file(path).get().asFile
    }

}

interface AllureReportExtension {
    /**
     * True to keep logcat files even if tests pass, or false to only pull them if tests fail.
     * Default: false
     */
    var attachLogcatOnSuccess: Boolean?

    /**
     * The report output path.
     * Can be used to merge reports of multiple modules by using the same directory
     */
    var outputDir: File?
}
