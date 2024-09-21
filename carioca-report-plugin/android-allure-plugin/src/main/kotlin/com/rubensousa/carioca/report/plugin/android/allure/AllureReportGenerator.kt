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

package com.rubensousa.carioca.report.plugin.android.allure

import com.google.common.io.Files
import com.rubensousa.carioca.report.json.JsonAttachment
import com.rubensousa.carioca.report.json.JsonExecutionReport
import com.rubensousa.carioca.report.json.JsonExecutionStatus
import com.rubensousa.carioca.report.json.JsonReportParser
import com.rubensousa.carioca.report.json.JsonStage
import com.rubensousa.carioca.report.json.JsonTestReport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.util.UUID

@OptIn(ExperimentalSerializationApi::class)
class AllureReportGenerator(
    private val logcatFinder: LogcatFileFinder,
    private val parser: JsonReportParser,
) {

    private val stageValue = "finished"
    private val brokenStatus = "broken"

    fun generateReport(
        testResultDir: File,
        logcatOutputDir: File,
        outputDir: File,
        keepLogcatOnSuccess: Boolean,
        deleteOriginalReports: Boolean
    ) {
        val reportDir = parser.findReportDir(testResultDir) ?: return
        val testReportFiles = parser.parseTestReports(reportDir)
        val logcatFiles = logcatFinder.find(logcatOutputDir)
        outputDir.mkdirs()
        testReportFiles.forEach { testReportFile ->
            val originalReport = createTestReport(testReportFile.report)
            val logcatFile = if (keepLogcatOnSuccess || originalReport.status == brokenStatus) {
                logcatFiles[originalReport.fullName]
            } else {
                null
            }
            moveTestReport(
                report = originalReport,
                inputDir = reportDir,
                outputDir = outputDir,
                logcatFile = logcatFile
            )
            if (deleteOriginalReports) {
                testReportFile.file.delete()
            }
            createContainerReport(testReportFile.report)?.let {
                moveContainerReport(it, testResultDir, outputDir)
            }
        }
    }

    private fun moveTestReport(
        report: AllureTestReport,
        inputDir: File,
        outputDir: File,
        logcatFile: File?,
    ) {
        val reportFile = File(outputDir, "${report.uuid}-result.json")
        var newReport = moveTestAttachments(report, inputDir, outputDir)
        if (logcatFile != null) {
            val newFile = moveAttachment(
                src = logcatFile,
                outputDir = outputDir,
                fileId = UUID.randomUUID().toString()
            )
            newReport = newReport.copy(
                attachments = newReport.attachments + listOf(
                    AllureAttachment(
                        name = "Logcat",
                        source = newFile.name,
                        type = "text/plain"
                    )
                )
            )
        }
        writeToFile(newReport, reportFile)
    }

    private fun moveTestAttachments(
        report: AllureTestReport,
        inputDir: File,
        outputDir: File,
    ): AllureTestReport {
        return report.copy(
            attachments = moveAttachments(report.attachments, inputDir, outputDir),
            steps = moveStepAttachments(report.steps, inputDir, outputDir)
        )
    }

    private fun moveStepAttachments(
        steps: List<AllureStep>,
        inputDir: File,
        outputDir: File,
    ): List<AllureStep> {
        val newSteps = mutableListOf<AllureStep>()
        steps.forEach { step ->
            newSteps.add(
                step.copy(
                    steps = moveStepAttachments(
                        steps = step.steps,
                        inputDir = inputDir,
                        outputDir = outputDir
                    ),
                    attachments = moveAttachments(
                        attachments = step.attachments,
                        inputDir = inputDir,
                        outputDir = outputDir
                    )
                )
            )
        }
        return newSteps
    }

    /**
     * Ensures all attachments are also renamed to match the allure spec of having "-attachment" as suffix
     */
    private fun moveAttachments(
        attachments: List<AllureAttachment>,
        inputDir: File,
        outputDir: File,
    ): List<AllureAttachment> {
        val newAttachments = mutableListOf<AllureAttachment>()
        attachments.forEach { attachment ->
            val attachmentFile = File(inputDir, attachment.source)
            if (attachmentFile.exists()) {
                val newFile = moveAttachment(
                    src = attachmentFile,
                    outputDir = outputDir,
                    fileId = attachmentFile.nameWithoutExtension
                )
                newAttachments.add(
                    attachment.copy(
                        source = newFile.name
                    )
                )
            }
        }
        return newAttachments
    }

    private fun moveAttachment(
        src: File,
        outputDir: File,
        fileId: String,
    ): File {
        val newFile = File(outputDir, fileId + "-attachment.${src.extension}")
        Files.move(src, newFile)
        return newFile
    }

    private fun moveContainerReport(report: AllureContainerReport, inputDir: File, outputDir: File) {
        val reportFile = File(outputDir, "${report.uuid}-container.json")
        val newReport = report.copy(
            befores = moveStepAttachments(report.befores, inputDir, outputDir),
            afters = moveStepAttachments(report.afters, inputDir, outputDir)
        )
        writeToFile(newReport, reportFile)
    }

    private inline fun <reified T> writeToFile(content: T, file: File) {
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            file.outputStream().use {
                Json.encodeToStream(content, it)
            }
        } catch (exception: Exception) {
            println(exception.stackTraceToString())
        }
    }

    private fun createTestReport(test: JsonTestReport): AllureTestReport {
        val execution = test.execution
        return AllureTestReport(
            uuid = execution.id,
            historyId = test.id,
            testCaseId = test.id,
            fullName = test.fullName,
            links = createLinks(test),
            labels = createLabels(test),
            name = test.title,
            description = test.description,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            steps = test.stages.map { mapStage(it) },
            attachments = mapAttachments(test.attachments),
            start = execution.startTime,
            stop = execution.endTime
        )
    }

    private fun createContainerReport(test: JsonTestReport): AllureContainerReport? {
        if (test.beforeStages.isEmpty() && test.afterStages.isEmpty()) {
            return null
        }
        return AllureContainerReport(
            uuid = UUID.randomUUID().toString(),
            name = test.title,
            children = listOf(test.execution.id),
            befores = test.beforeStages.map { mapStage(it) },
            afters = test.afterStages.map { mapStage(it) },
            start = test.execution.startTime,
            stop = test.execution.endTime
        )
    }

    private fun mapStage(stage: JsonStage): AllureStep {
        val execution = stage.execution
        return AllureStep(
            name = stage.name,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = mapAttachments(stage.attachments),
            start = execution.startTime,
            stop = execution.endTime,
            steps = stage.stages.map { step ->
                mapStage(step)
            },
            parameters = stage.parameters.map {
                AllureParameter(
                    name = it.key,
                    value = it.value
                )
            }
        )
    }

    private fun mapAttachments(list: List<JsonAttachment>): List<AllureAttachment> {
        return list.map { attachment ->
            AllureAttachment(
                name = attachment.description,
                source = attachment.path,
                type = attachment.mimeType
            )
        }
    }

    private fun getStatus(reportStatus: JsonExecutionStatus): String {
        return when (reportStatus) {
            JsonExecutionStatus.PASSED -> "passed"
            JsonExecutionStatus.IGNORED -> "skipped"
            else -> brokenStatus
        }
    }

    private fun createLabels(test: JsonTestReport): List<AllureLabel> {
        return listOf(
            AllureLabel(
                name = "package",
                value = test.packageName
            ),
            AllureLabel(
                name = "testClass",
                value = test.className
            ),
            AllureLabel(
                name = "testMethod",
                value = test.methodName
            ),
            AllureLabel(
                name = "suite",
                value = test.className
            ),
            AllureLabel(
                name = "framework",
                value = "junit4"
            ),
            AllureLabel(
                name = "language",
                value = "kotlin"
            ),
        )
    }

    private fun createLinks(test: JsonTestReport): List<AllureLink> {
        return test.links.map { link ->
            AllureLink(
                name = link,
                url = link,
                type = "general"
            )
        }
    }

    private fun getStatusDetail(report: JsonExecutionReport): AllureStatusDetail? {
        val failureMessage = report.failureMessage
        val failureStacktrace = report.failureStacktrace.orEmpty()
        return if (failureMessage == null) {
            null
        } else {
            AllureStatusDetail(
                known = false,
                muted = false,
                flaky = false,
                message = failureMessage,
                trace = failureStacktrace
            )
        }
    }

}
