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

package com.rubensousa.carioca.android.report

import com.rubensousa.carioca.android.report.stage.InstrumentedBeforeAfterReport
import com.rubensousa.carioca.android.report.stage.InstrumentedScenarioReport
import com.rubensousa.carioca.android.report.stage.InstrumentedStepReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.stage.StageAttachment
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.report.junit4.ExecutionMetadata
import com.rubensousa.carioca.report.junit4.ReportProperty
import com.rubensousa.carioca.report.junit4.ReportStatus
import com.rubensousa.carioca.report.junit4.StageReport
import com.rubensousa.carioca.report.junit4.TestMetadata
import com.rubensousa.carioca.report.serialization.ExecutionReport
import com.rubensousa.carioca.report.serialization.ExecutionStatus
import com.rubensousa.carioca.report.serialization.ReportAttachment
import com.rubensousa.carioca.report.serialization.ReportFiles
import com.rubensousa.carioca.report.serialization.Stage
import com.rubensousa.carioca.report.serialization.TestReport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream

class DefaultInstrumentedReporter : CariocaInstrumentedReporter {

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        explicitNulls = false
    }

    override fun getOutputDir(metadata: TestMetadata): String {
        return ReportFiles.REPORT_DIR
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeTestReport(
        test: InstrumentedTestReport,
        storageProvider: ReportStorageProvider,
    ) {
        val metadata = test.getExecutionMetadata()
        val filePath = "${test.outputPath}/${metadata.uniqueId}_test_report.json"
        val jsonReport = buildTestReport(test)
        BufferedOutputStream(storageProvider.getOutputStream(filePath)).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    private fun buildTestReport(test: InstrumentedTestReport): TestReport {
        val metadata = test.metadata
        val testId = test.getProperty(ReportProperty.Id) ?: metadata.fullName
        val testTitle = test.getProperty(ReportProperty.Title) ?: metadata.methodName
        return TestReport(
            id = testId,
            title = testTitle,
            description = test.getProperty(ReportProperty.Description),
            packageName = metadata.packageName,
            className = metadata.className,
            methodName = metadata.methodName,
            fullName = metadata.fullName,
            links = test.getProperty<List<String>>(ReportProperty.Links).orEmpty(),
            execution = mapExecutionReport(test.getExecutionMetadata()),
            attachments = mapAttachments(test.getAttachments()),
            beforeStages = test.getStagesBefore().mapNotNull { stage ->
                buildStageReport(stage)
            },
            stages = test.getTestStages().mapNotNull { stage ->
                buildStageReport(stage)
            },
            afterStages = test.getStagesAfter().mapNotNull { stage ->
                buildStageReport(stage)
            },
        )
    }

    private fun buildStageReport(stage: StageReport): Stage? {
        return when (stage) {
            is InstrumentedStepReport -> buildStepReport(stage)
            is InstrumentedScenarioReport -> buildScenarioReport(stage)
            is InstrumentedBeforeAfterReport -> buildBeforeAfterReport(stage)
            else -> null
        }
    }

    private fun mapAttachments(attachments: List<StageAttachment>): List<ReportAttachment> {
        return attachments.map { attachment ->
            ReportAttachment(
                description = attachment.description,
                path = getAttachmentPath(attachment.path),
                mimeType = attachment.mimeType
            )
        }
    }

    // Ensures that all the attachments paths are relative to the same directory
    private fun getAttachmentPath(path: String): String {
        return path.replace("/${ReportFiles.REPORT_DIR}/", "")
    }

    private fun mapExecutionReport(execution: ExecutionMetadata): ExecutionReport {
        return ExecutionReport(
            id = execution.uniqueId,
            status = when (execution.status) {
                ReportStatus.PASSED -> ExecutionStatus.PASSED
                ReportStatus.FAILED -> ExecutionStatus.FAILED
                else -> ExecutionStatus.IGNORED
            },
            startTime = execution.startTime,
            endTime = execution.endTime,
            failureStacktrace = execution.failureCause?.stackTraceToString(),
            failureMessage = execution.failureCause?.message
        )
    }

    private fun buildStepReport(step: InstrumentedStepReport): Stage {
        val execution = step.getExecutionMetadata()
        val nestedStages = mutableListOf<Stage>()
        step.getTestStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        return Stage(
            id = step.id,
            name = step.title,
            type = "step",
            execution = mapExecutionReport(execution),
            attachments = mapAttachments(step.getAttachments()),
            stages = nestedStages,
        )
    }

    private fun buildScenarioReport(scenario: InstrumentedScenarioReport): Stage {
        val nestedStages = mutableListOf<Stage>()
        scenario.getTestStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        return Stage(
            id = scenario.id,
            name = scenario.title,
            type = "scenario",
            execution = mapExecutionReport(scenario.getExecutionMetadata()),
            attachments = mapAttachments(scenario.getAttachments()),
            stages = nestedStages,
        )
    }

    private fun buildBeforeAfterReport(
        beforeAfterReport: InstrumentedBeforeAfterReport,
    ): Stage {
        val nestedStages = mutableListOf<Stage>()
        beforeAfterReport.getTestStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        val executionMetadata = beforeAfterReport.getExecutionMetadata()
        return Stage(
            id = executionMetadata.uniqueId,
            name = beforeAfterReport.title,
            type = if (beforeAfterReport.before) {
                "before"
            } else {
                "after"
            },
            execution = mapExecutionReport(executionMetadata),
            attachments = mapAttachments(beforeAfterReport.getAttachments()),
            stages = nestedStages,
        )
    }
}
