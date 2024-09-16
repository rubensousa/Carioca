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

import com.google.common.io.Files
import com.rubensousa.carioca.report.serialization.ExecutionReport
import com.rubensousa.carioca.report.serialization.ExecutionStatus
import com.rubensousa.carioca.report.serialization.ReportAttachment
import com.rubensousa.carioca.report.serialization.ReportParser
import com.rubensousa.carioca.report.serialization.Stage
import com.rubensousa.carioca.report.serialization.TestReport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.util.UUID

@OptIn(ExperimentalSerializationApi::class)
class AllureReportGenerator(
    private val parser: ReportParser = ReportParser(),
) {

    private val stageValue = "finished"

    fun generateReport(inputDir: File, outputDir: File) {
        val reportDir = parser.findReportDir(inputDir) ?: return
        val testReports = parser.parseTests(reportDir)
        testReports.forEach { testReport ->
            val allureTestReport = createTestReport(testReport)
            moveTestReport(allureTestReport, reportDir, outputDir)
            createContainerReport(testReport)?.let {
                moveContainerReport(it, outputDir)
            }
        }
    }

    private fun moveTestReport(
        report: AllureTestReport,
        inputDir: File,
        outputDir: File,
    ) {
        val reportFile = File(outputDir, "${report.uuid}-result.json")
        writeToFile(report, reportFile)
        report.attachments.forEach { attachment ->
            val attachmentFile = File(inputDir, attachment.source)
            if (attachmentFile.exists()) {
                val dstFile = File(outputDir, attachmentFile.name)
                Files.copy(attachmentFile, dstFile)
            }
        }
    }

    private fun moveContainerReport(report: AllureContainerReport, outputDir: File) {
        val reportFile = File(outputDir, "${report.uuid}-container.json")
        writeToFile(report, reportFile)
    }

    private inline fun <reified T> writeToFile(content: T, file: File) {
        try {
            file.outputStream().use {
                Json.encodeToStream(content, it)
            }
        } catch (exception: Exception) {
            println(exception.stackTraceToString())
        }
    }

    private fun createTestReport(test: TestReport): AllureTestReport {
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

    private fun createContainerReport(test: TestReport): AllureContainerReport? {
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

    private fun mapStage(stage: Stage): AllureStep {
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
            }
        )
    }

    private fun mapAttachments(list: List<ReportAttachment>): List<AllureAttachment> {
        return list.map { attachment ->
            AllureAttachment(
                name = attachment.description,
                source = attachment.path,
                type = attachment.mimeType
            )
        }
    }

    private fun getStatus(reportStatus: ExecutionStatus): String {
        return when (reportStatus) {
            ExecutionStatus.PASSED -> "passed"
            ExecutionStatus.IGNORED -> "skipped"
            else -> "broken"
        }
    }

    private fun createLabels(test: TestReport): List<AllureLabel> {
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

    private fun createLinks(test: TestReport): List<AllureLink> {
        return test.links.map { link ->
            AllureLink(
                name = link,
                url = link,
                type = "general"
            )
        }
    }

    private fun getStatusDetail(report: ExecutionReport): AllureStatusDetail? {
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
