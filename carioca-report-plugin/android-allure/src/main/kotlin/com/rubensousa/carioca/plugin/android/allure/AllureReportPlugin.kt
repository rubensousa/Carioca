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

package com.rubensousa.carioca.plugin.android.allure

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import java.io.File

class AllureReportPlugin : Plugin<Project> {

    private val testOutputDir = "outputs/connected_android_test_additional_output"
    private val outputDir = "outputs/allure-results"
    private val supportedPlugins = listOf(
        "com.android.application",
        "com.android.library",
        "com.android.dynamic-feature"
    )
    private val reportGenerator = AllureReportGenerator()

    override fun apply(target: Project) {
        target.extensions.add("allureReport", AllureReportExtension::class.java)

        target.afterEvaluate {
            check(supportedPlugins.any { target.plugins.hasPlugin(it) }) {
                "Report generation only works for modules of type: $supportedPlugins"
            }
            val extension = extensions.getByType(AllureReportExtension::class.java)
            registerTask(target, extension)
        }
    }

    private fun registerTask(project: Project, extension: AllureReportExtension?) {
        val outputPath = project.layout.buildDirectory.file(outputDir).get().asFile.path
        val outputDir = File(outputPath)
        val dependentTasks = extension?.testTask ?: "connectedDebugAndroidTest"

        val testOutputDir = project.layout.buildDirectory.file(testOutputDir).get().asFile
        testOutputDir.mkdirs()
        val cleanTask = project.tasks.register("cleanAllureReport") {
            description = "Deletes the previous generated allure report"
            doFirst {
                // Clean-up all the files from the output dirs
                // to avoid conflicts with the next report generation
                outputDir.deleteRecursively()
                testOutputDir.deleteRecursively()
            }
        }
        // TODO: Add support for variants
        project.tasks.register("connectedAllureReport") {
            group = VERIFICATION_GROUP
            description = "Runs android tests and generates the allure report"
            dependsOn(dependentTasks)
            dependsOn(cleanTask)

            doLast {
                outputDir.mkdirs()
                reportGenerator.generateReport(
                    inputDir = testOutputDir,
                    outputDir = outputDir
                )
                println("Allure report generated in $outputPath")
            }
        }
        project.tasks.register("generateAllureReport") {
            group = "report"
            description = "Generates the allure report for a previous test run"

            doLast {
                outputDir.deleteRecursively()
                reportGenerator.generateReport(
                    inputDir = testOutputDir,
                    outputDir = outputDir
                )
                println("Allure report generated in $outputPath")
            }
        }
    }

}


interface AllureReportExtension {
    var testTask: String?
}
