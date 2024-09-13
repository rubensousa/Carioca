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
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.StageAttachment
import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenario
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStep
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTest
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import com.rubensousa.carioca.junit.report.ExecutionMetadata
import com.rubensousa.carioca.junit.report.ExecutionStatus
import com.rubensousa.carioca.junit.report.ReportProperty
import com.rubensousa.carioca.junit.report.StageReport
import com.rubensousa.carioca.junit.report.TestMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream
import java.io.OutputStream

class AllureInstrumentedReporter : CariocaInstrumentedReporter {

    private val stageValue = "finished"
    private val dirName = "allure-results"

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        explicitNulls = false
    }

    override fun getOutputDir(metadata: TestMetadata): String {
        return dirName
    }

    override fun getReportFilename(test: InstrumentedTest): String {
        return test.getExecutionMetadata().uniqueId + "-result.json"
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
    override fun writeTestReport(test: InstrumentedTest, outputStream: OutputStream) {
        val allureReport = createTestReport(test)
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

    private fun createTestReport(test: InstrumentedTest): AllureReport {
        val metadata = test.metadata
        val execution = test.getExecutionMetadata()
        val testId = test.getProperty(ReportProperty.Id) ?: metadata.fullName
        val testTitle = test.getProperty(ReportProperty.Title) ?: metadata.methodName
        return AllureReport(
            uuid = execution.uniqueId,
            historyId = testId,
            testCaseId = testId,
            fullName = metadata.fullName,
            links = createLinks(test),
            labels = createLabels(metadata),
            name = testTitle,
            description = test.getProperty(ReportProperty.Description),
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            steps = test.getStages().mapNotNull { mapStage(it) },
            attachments = getAttachments(test),
            start = execution.startTime,
            stop = execution.endTime
        )
    }

    private fun createSuiteReport(report: TestSuiteReport): AllureReport {
        val execution = report.executionMetadata
        return AllureReport(
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

    private fun mapScenario(scenario: InstrumentedScenario): AllureStep {
        val metadata = scenario.getMetadata()
        val execution = scenario.getExecutionMetadata()
        return AllureStep(
            name = metadata.title,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = emptyList(),
            start = execution.startTime,
            stop = execution.endTime,
            steps = scenario.getStages().mapNotNull { step ->
                mapStage(step)
            }
        )
    }

    private fun mapStage(stage: StageReport): AllureStep? {
        return when (stage) {
            is InstrumentedStep -> mapStep(stage)
            is InstrumentedScenario -> mapScenario(stage)
            is InstrumentedStageReport -> mapUnknownStage(stage)
            else -> null
        }
    }

    private fun mapStep(step: InstrumentedStep): AllureStep {
        val metadata = step.getMetadata()
        val execution = step.getExecutionMetadata()
        return AllureStep(
            name = metadata.title,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = getAttachments(step),
            start = execution.startTime,
            stop = execution.endTime,
            steps = step.getStages().mapNotNull { stage ->
                mapStage(stage)
            }
        )
    }

    private fun mapUnknownStage(stage: InstrumentedStageReport): AllureStep {
        val execution = stage.getExecutionMetadata()
        return AllureStep(
            name = stage.toString(),
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = getAttachments(stage),
            start = execution.startTime,
            stop = execution.endTime,
            steps = stage.getStages().mapNotNull { childStage -> mapStage(childStage) }
        )
    }

    private fun getAttachments(stage: InstrumentedStageReport): List<AllureAttachment> {
        return mapAttachments(stage.getAttachments())
    }

    private fun mapAttachments(list: List<StageAttachment>): List<AllureAttachment> {
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

    private fun createLinks(test: InstrumentedTest): List<AllureLink> {
        val links = test.getProperty<List<String>>(ReportProperty.Links) ?: return emptyList()
        return links.map { link ->
            AllureLink(
                name = link,
                url = link,
                type = "general"
            )
        }
    }

    private fun getStatusDetail(metadata: ExecutionMetadata): AllureStatusDetail? {
        val failureCause = metadata.failureCause
        return if (failureCause == null) {
            null
        } else {
            AllureStatusDetail(
                known = false,
                muted = false,
                flaky = false,
                message = failureCause.message.orEmpty(),
                trace = failureCause.stackTraceToString()
            )
        }
    }

}