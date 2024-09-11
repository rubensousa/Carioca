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

package com.rubensousa.carioca.report.allure

import com.rubensousa.carioca.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.report.ReportAttachment
import com.rubensousa.carioca.report.stage.ExecutionStatus
import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
import com.rubensousa.carioca.report.stage.TestReportMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream
import java.io.OutputStream

class CariocaAllureInstrumentedReporter : CariocaInstrumentedReporter {

    private val stageValue = "finished"
    private val dirName = "allure-results"

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
    }

    override fun getOutputDir(report: TestReport): String {
        return dirName
    }

    override fun getReportFilename(report: TestReport): String {
        return report.getMetadata().execution.uniqueId + "-result.json"
    }

    override fun getScreenshotName(id: String): String {
        return getAttachmentName(id)
    }

    override fun getRecordingName(id: String): String {
        return getAttachmentName(id)
    }

    private fun getAttachmentName(id: String): String {
        return "$id-attachment"
    }

    @ExperimentalSerializationApi
    override fun writeTestReport(report: TestReport, outputStream: OutputStream) {
        val allureReport = createReport(report)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(allureReport, stream)
            stream.flush()
        }
    }

    private fun createReport(report: TestReport): CariocaAllureReport {
        val metadata = report.getMetadata()
        val execution = metadata.execution
        return CariocaAllureReport(
            uuid = execution.uniqueId,
            historyId = metadata.testId,
            testCaseId = metadata.testId,
            fullName = metadata.getTestFullName(),
            links = emptyList(),
            labels = createLabels(metadata),
            name = metadata.testTitle,
            status = getStatus(execution.status),
            statusDetails = execution.failureCause?.let { error ->
                AllureStatusDetail(
                    known = false,
                    muted = false,
                    flaky = false,
                    message = error.message.orEmpty(),
                    trace = error.stackTraceToString()
                )
            },
            stage = stageValue,
            steps = createSteps(report),
            attachments = getAttachments(report),
            start = execution.startTime,
            stop = execution.endTime
        )
    }

    private fun createSteps(report: TestReport): List<AllureStep> {
        val steps = mutableListOf<AllureStep>()
        val stageReports = report.getStageReports()
        stageReports.forEach { stageReport ->
            if (stageReport is ScenarioReport) {
                steps.add(createScenarioStep(stageReport))
            } else if (stageReport is StepReport) {
                steps.add(createStep(stageReport))
            }
        }
        return steps
    }

    private fun createScenarioStep(report: ScenarioReport): AllureStep {
        val metadata = report.getMetadata()
        val execution = metadata.execution
        return AllureStep(
            name = metadata.name,
            status = getStatus(execution.status),
            statusDetails = null,
            stage = stageValue,
            attachments = emptyList(),
            start = execution.startTime,
            stop = execution.endTime,
            steps = report.getSteps().map { step ->
                createStep(step)
            }
        )
    }

    private fun createStep(report: StepReport): AllureStep {
        val metadata = report.getMetadata()
        val execution = metadata.execution
        return AllureStep(
            name = metadata.title,
            status = getStatus(execution.status),
            statusDetails = null,
            stage = stageValue,
            attachments = getAttachments(report),
            start = execution.startTime,
            stop = execution.endTime,
            steps = report.getSteps().map { step ->
                createStep(step)
            }
        )
    }

    private fun getAttachments(report: StepReport): List<AllureAttachment> {
        return mapAttachments(report.getAttachments())
    }

    private fun getAttachments(report: TestReport): List<AllureAttachment> {
        return mapAttachments(report.getAttachments())
    }

    private fun mapAttachments(list: List<ReportAttachment>): List<AllureAttachment> {
        return list.map { attachment ->
            AllureAttachment(
                name = attachment.description,
                source = getAttachmentPath(attachment.path),
                type = attachment.mimeType
            )
        }
    }

    // Ensures that all the attachments paths are relative to the same directory
    private fun getAttachmentPath(path: String): String {
        return path.replace("/$dirName/", "")
    }

    private fun getStatus(reportStatus: ExecutionStatus): String {
        return if (reportStatus == ExecutionStatus.PASSED) {
            "passed"
        } else {
            "broken"
        }
    }

    private fun createLabels(metadata: TestReportMetadata): List<AllureLabel> {
        return listOf(
            AllureLabel(
                name = "package",
                value = metadata.packageName
            ),
            AllureLabel(
                name = "testClass",
                value = metadata.className
            ),
            AllureLabel(
                name = "testMethod",
                value = metadata.methodName
            ),
            AllureLabel(
                name = "suite",
                value = metadata.className
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

}
