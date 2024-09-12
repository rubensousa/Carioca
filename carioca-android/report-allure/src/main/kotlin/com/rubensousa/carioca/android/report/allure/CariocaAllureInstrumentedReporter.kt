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

package com.rubensousa.carioca.android.report.allure

import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.ReportAttachment
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenarioStage
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepStage
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTestStage
import com.rubensousa.carioca.android.report.stage.test.TestMetadata
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import com.rubensousa.carioca.stage.CariocaStage
import com.rubensousa.carioca.stage.ExecutionStatus
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

    override fun getOutputDir(stage: InstrumentedTestStage): String {
        return dirName
    }

    override fun getReportFilename(stage: InstrumentedTestStage): String {
        return stage.getExecutionMetadata().uniqueId + "-result.json"
    }

    override fun getSuiteReportFilePath(report: TestSuiteReport): String {
        return "${dirName}/${report.executionMetadata.uniqueId}-result.json"
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
    override fun writeTestReport(stage: InstrumentedTestStage, outputStream: OutputStream) {
        val allureReport = createTestReport(stage)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(allureReport, stream)
            stream.flush()
        }
    }

    @ExperimentalSerializationApi
    override fun writeSuiteReport(report: TestSuiteReport, outputStream: OutputStream) {
        val allureReport = createSuiteReport(report)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(allureReport, stream)
            stream.flush()
        }
    }

    private fun createTestReport(report: InstrumentedTestStage): CariocaAllureReport {
        val metadata = report.getMetadata()
        val execution = report.getExecutionMetadata()
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

    private fun createSuiteReport(report: TestSuiteReport): CariocaAllureReport {
        val execution = report.executionMetadata
        return CariocaAllureReport(
            uuid = execution.uniqueId,
            historyId = report.packageName,
            testCaseId = report.packageName,
            fullName = report.packageName,
            links = emptyList(),
            labels = listOf(
                AllureLabel(
                    name = "package",
                    value = report.packageName
                ),
                AllureLabel(
                    name = "suite",
                    value = report.packageName,
                ),
                AllureLabel(
                    name = "framework",
                    value = "junit4"
                ),
                AllureLabel(
                    name = "language",
                    value = "kotlin"
                ),
            ),
            name = "Suite for ${report.packageName}",
            status = getStatus(execution.status),
            statusDetails = null,
            stage = stageValue,
            steps = emptyList(),
            attachments = emptyList(),
            start = execution.startTime,
            stop = execution.endTime
        )
    }

    private fun createSteps(report: InstrumentedTestStage): List<AllureStep> {
        val steps = mutableListOf<AllureStep>()
        val stages = report.getStages()
        stages.forEach { stage ->
            mapStage(stage)?.let { steps.add(it) }
        }
        return steps
    }

    private fun mapScenario(report: InstrumentedScenarioStage): AllureStep {
        val metadata = report.getMetadata()
        val execution = report.getExecutionMetadata()
        return AllureStep(
            name = metadata.name,
            status = getStatus(execution.status),
            statusDetails = null,
            stage = stageValue,
            attachments = emptyList(),
            start = execution.startTime,
            stop = execution.endTime,
            steps = report.getSteps().map { step ->
                mapStep(step)
            }
        )
    }

    private fun mapStage(stage: CariocaStage): AllureStep? {
        return when (stage) {
            is InstrumentedStepStage -> mapStep(stage)
            is InstrumentedScenarioStage -> mapScenario(stage)
            else -> null
        }
    }

    private fun mapStep(step: InstrumentedStepStage): AllureStep {
        val metadata = step.getMetadata()
        val execution = step.getExecutionMetadata()
        return AllureStep(
            name = metadata.title,
            status = getStatus(execution.status),
            statusDetails = null,
            stage = stageValue,
            attachments = getAttachments(step),
            start = execution.startTime,
            stop = execution.endTime,
            steps = step.getStages().mapNotNull { stage ->
                mapStage(stage)
            }
        )
    }

    private fun getAttachments(report: InstrumentedStepStage): List<AllureAttachment> {
        return mapAttachments(report.getAttachments())
    }

    private fun getAttachments(report: InstrumentedTestStage): List<AllureAttachment> {
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

    private fun createLabels(metadata: TestMetadata): List<AllureLabel> {
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
