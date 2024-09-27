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

import com.rubensousa.carioca.report.json.JsonReportParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import java.io.File

class AllureReportPlugin : Plugin<Project> {

    private val logcatOutputDirPath = "outputs/androidTest-results"
    private val testOutputDirPath = "outputs/connected_android_test_additional_output"
    private val outputDirPath = "outputs/allure-results"
    private val supportedPlugins = listOf(
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

        target.afterEvaluate {
            check(supportedPlugins.any { target.plugins.hasPlugin(it) }) {
                "Report generation only works for modules of type: $supportedPlugins"
            }
            val extension = target.extensions.getByType(AllureReportExtension::class.java)
            registerTasks(target, extension)
        }
    }

    private fun registerTasks(project: Project, extension: AllureReportExtension?) {
        val buildOutputDir = project.layout.buildDirectory.file(outputDirPath).get().asFile
        val outputDir = extension?.outputDir ?: buildOutputDir
        outputDir.mkdirs()
        val testTask = extension?.testTask ?: "connectedDebugAndroidTest"

        val testOutputDir = project.layout.buildDirectory.file(testOutputDirPath).get().asFile
        val logcatOutputDir = project.layout.buildDirectory.file(logcatOutputDirPath).get().asFile
        val keepLogcatOnSuccess = extension?.keepLogcatOnSuccess ?: false
        testOutputDir.mkdirs()
        project.tasks.register("cleanAllureReport") {
            it.group = "report"
            it.description = "Deletes the previous generated allure report"
            it.doFirst {
                // Clean-up all the files from the output dirs
                // to avoid conflicts with the next report generation
                outputDir.deleteRecursively()
                testOutputDir.deleteRecursively()
            }
        }

        val generateTask = project.tasks.register("generateAllureReport") {
            it.group = "report"
            it.description = "Generates the allure report for a previous test run"
            it.doLast {
                outputDir.deleteRecursively()
                reportGenerator.generateReport(
                    testResultDir = testOutputDir,
                    logcatOutputDir = logcatOutputDir,
                    outputDir = outputDir,
                    keepLogcatOnSuccess = keepLogcatOnSuccess,
                )
                println("Allure report generated in file:///$outputDirPath")
            }
        }

        // TODO: Add support for variants
        project.tasks.register("connectedAllureReport") {
            it.group = VERIFICATION_GROUP
            it.description = "Runs android tests and generates the allure report"
            it.dependsOn(testTask)
        }
        // Ensures the report is generated even if the test task fails
        project.tasks.findByName(testTask)?.finalizedBy(generateTask)
    }

}


interface AllureReportExtension {
    /**
     * The name of the test task that will be invoked to generate the report
     */
    var testTask: String?

    /**
     * True to keep logcat files even if tests pass, or false to only pull them if tests fail.
     * Default: false
     */
    var keepLogcatOnSuccess: Boolean?

    /**
     * The report output path.
     * Can be used to merge reports of multiple modules by using the same directory
     */
    var outputDir: File?
}
