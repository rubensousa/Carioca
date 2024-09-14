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
import com.rubensousa.carioca.android.report.stage.InstrumentedBeforeAfterReport
import com.rubensousa.carioca.android.report.stage.InstrumentedScenarioReport
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.InstrumentedStepReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.stage.StageAttachment
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import com.rubensousa.carioca.junit.report.ExecutionIdGenerator
import com.rubensousa.carioca.junit.report.ExecutionMetadata
import com.rubensousa.carioca.junit.report.ReportProperty
import com.rubensousa.carioca.junit.report.ReportStatus
import com.rubensousa.carioca.junit.report.StageReport
import com.rubensousa.carioca.junit.report.TestMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream

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

    override fun getScreenshotName(id: String): String {
        return getAttachmentName(id)
    }

    override fun getRecordingName(id: String): String {
        return getAttachmentName(id)
    }

    private fun getAttachmentName(id: String): String {
        return "$id-attachment"
    }

    private fun getTestReportPath(test: InstrumentedTestReport): String {
        val filename = test.getExecutionMetadata().uniqueId + "-result.json"
        return "${test.outputPath}/$filename"
    }

    @ExperimentalSerializationApi
    override fun writeTestReport(
        test: InstrumentedTestReport,
        storageProvider: ReportStorageProvider,
    ) {
        // First save the test report
        writeTestReport(
            report = createTestReport(test),
            storageProvider = storageProvider,
            destPath = getTestReportPath(test)
        )

        // Now save the container if we have before or after stages
        if (test.getStagesBefore().isNotEmpty() || test.getStagesAfter().isNotEmpty()) {
            val report = createContainerReport(test)
            writeContainerReport(
                report = createContainerReport(test),
                storageProvider = storageProvider,
                destPath = "${test.outputPath}/${report.uuid}-container.json"
            )
        }
    }

    @ExperimentalSerializationApi
    private fun writeTestReport(
        report: AllureTestReport,
        storageProvider: ReportStorageProvider,
        destPath: String,
    ) {
        val testOutputStream = storageProvider.getOutputStream(destPath)
        BufferedOutputStream(testOutputStream).use { stream ->
            json.encodeToStream(report, stream)
            stream.flush()
        }
    }

    @ExperimentalSerializationApi
    private fun writeContainerReport(
        report: AllureContainerReport,
        storageProvider: ReportStorageProvider,
        destPath: String,
    ) {
        val testOutputStream = storageProvider.getOutputStream(destPath)
        BufferedOutputStream(testOutputStream).use { stream ->
            json.encodeToStream(report, stream)
            stream.flush()
        }
    }

    @ExperimentalSerializationApi
    override fun writeSuiteReport(
        report: TestSuiteReport,
        storageProvider: ReportStorageProvider,
    ) {
        val filePath = "${dirName}/${report.executionMetadata.uniqueId}-result.json"
        val allureReport = createSuiteReport(report)
        BufferedOutputStream(storageProvider.getOutputStream(filePath)).use { stream ->
            json.encodeToStream(allureReport, stream)
            stream.flush()
        }
    }

    private fun createTestReport(test: InstrumentedTestReport): AllureTestReport {
        val metadata = test.metadata
        val execution = test.getExecutionMetadata()
        val testId = test.getProperty(ReportProperty.Id) ?: metadata.fullName
        val testTitle = test.getProperty(ReportProperty.Title) ?: metadata.methodName
        return AllureTestReport(
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
            steps = test.getTestStages().mapNotNull { mapStage(it) },
            attachments = getAttachments(test),
            start = execution.startTime,
            stop = execution.endTime
        )
    }

    private fun createContainerReport(test: InstrumentedTestReport): AllureContainerReport {
        val execution = test.getExecutionMetadata()
        return AllureContainerReport(
            uuid = ExecutionIdGenerator.get(),
            name = test.metadata.methodName,
            children = listOf(execution.uniqueId),
            befores = test.getStagesBefore().mapNotNull { mapStage(it) },
            afters = test.getStagesAfter().mapNotNull { mapStage(it) },
            start = execution.startTime,
            stop = execution.endTime
        )
    }

    private fun createSuiteReport(report: TestSuiteReport): AllureTestReport {
        val execution = report.executionMetadata
        return AllureTestReport(
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

    private fun mapScenario(scenario: InstrumentedScenarioReport): AllureStep {
        val execution = scenario.getExecutionMetadata()
        return AllureStep(
            name = scenario.title,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = emptyList(),
            start = execution.startTime,
            stop = execution.endTime,
            steps = scenario.getTestStages().mapNotNull { step ->
                mapStage(step)
            }
        )
    }

    private fun mapStage(stage: StageReport): AllureStep? {
        return when (stage) {
            is InstrumentedStepReport -> mapStep(stage)
            is InstrumentedScenarioReport -> mapScenario(stage)
            is InstrumentedBeforeAfterReport -> mapBeforeAfter(stage)
            is InstrumentedStageReport -> mapUnknownStage(stage)
            else -> null
        }
    }

    private fun mapStep(step: InstrumentedStepReport): AllureStep {
        val execution = step.getExecutionMetadata()
        return AllureStep(
            name = step.title,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = getAttachments(step),
            start = execution.startTime,
            stop = execution.endTime,
            steps = step.getTestStages().mapNotNull { stage ->
                mapStage(stage)
            }
        )
    }

    private fun mapBeforeAfter(step: InstrumentedBeforeAfterReport): AllureStep {
        val execution = step.getExecutionMetadata()
        return AllureStep(
            name = step.title,
            status = getStatus(execution.status),
            statusDetails = getStatusDetail(execution),
            stage = stageValue,
            attachments = getAttachments(step),
            start = execution.startTime,
            stop = execution.endTime,
            steps = step.getTestStages().mapNotNull { stage ->
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
            steps = stage.getTestStages().mapNotNull { childStage -> mapStage(childStage) }
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

    private fun getStatus(reportStatus: ReportStatus): String {
        return when (reportStatus) {
            ReportStatus.PASSED -> "passed"
            ReportStatus.SKIPPED -> "skipped"
            else -> "broken"
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

    private fun createLinks(test: InstrumentedTestReport): List<AllureLink> {
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
