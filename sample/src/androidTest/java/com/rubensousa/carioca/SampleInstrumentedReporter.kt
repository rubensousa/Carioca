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

package com.rubensousa.carioca

import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.stage.InstrumentedBeforeAfterReport
import com.rubensousa.carioca.android.report.stage.InstrumentedScenarioReport
import com.rubensousa.carioca.android.report.stage.InstrumentedStepReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.stage.StageAttachment
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import com.rubensousa.carioca.junit.report.ExecutionMetadata
import com.rubensousa.carioca.junit.report.ReportProperty
import com.rubensousa.carioca.junit.report.StageReport
import com.rubensousa.carioca.junit.report.TestMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream

/**
 * Represents the test report in a json format that can be used for analysing the test output
 */
class SampleJsonInstrumentedReporter : CariocaInstrumentedReporter {

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        explicitNulls = false
    }

    override fun getOutputDir(metadata: TestMetadata): String {
        return "${metadata.className}/${metadata.methodName}"
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeTestReport(
        test: InstrumentedTestReport,
        storageProvider: ReportStorageProvider,
    ) {
        val metadata = test.getExecutionMetadata()
        val filePath = "${test.outputPath}/${metadata.uniqueId}_report.json"
        val jsonReport = buildTestReport(test)
        BufferedOutputStream(storageProvider.getOutputStream(filePath)).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    @ExperimentalSerializationApi
    override fun writeSuiteReport(
        report: TestSuiteReport,
        storageProvider: ReportStorageProvider,
    ) {
        val jsonReport = buildSuiteReport(report)
        BufferedOutputStream(storageProvider.getOutputStream("suite_report.json")).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    private fun buildTestReport(test: InstrumentedTestReport): CariocaJsonTestReport {
        val metadata = test.metadata
        val testId = test.getProperty(ReportProperty.Id) ?: metadata.fullName
        val testTitle = test.getProperty(ReportProperty.Title) ?: metadata.methodName
        return CariocaJsonTestReport(
            testId = testId,
            testDescription = testTitle,
            testClass = metadata.className,
            testName = metadata.methodName,
            testFullName = metadata.fullName,
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

    private fun buildSuiteReport(report: TestSuiteReport): CariocaJsonSuiteReport {
        return CariocaJsonSuiteReport(
            packageName = report.packageName,
            execution = mapExecutionReport(report.executionMetadata),
            testStatus = report.testStatus.map {
                SuiteTestStatus(
                    status = it.key.name,
                    total = it.value
                )
            }
        )
    }

    private fun buildStageReport(stage: StageReport): StageJsonReport? {
        return when (stage) {
            is InstrumentedStepReport -> buildStepReport(stage)
            is InstrumentedScenarioReport -> buildScenarioReport(stage)
            else -> null
        }
    }

    private fun mapAttachments(attachments: List<StageAttachment>): List<AttachmentJsonReport> {
        return attachments.map { attachment ->
            AttachmentJsonReport(
                description = attachment.description,
                path = attachment.path,
                mimeType = attachment.mimeType
            )
        }
    }

    private fun mapExecutionReport(execution: ExecutionMetadata): ExecutionJsonReport {
        return ExecutionJsonReport(
            id = execution.uniqueId,
            status = execution.status.name,
            startTime = execution.startTime,
            endTime = execution.endTime,
            failureStacktrace = execution.failureCause?.stackTraceToString(),
            failureMessage = execution.failureCause?.message
        )
    }

    private fun buildStepReport(step: InstrumentedStepReport): StageJsonReport {
        val execution = step.getExecutionMetadata()
        val nestedStages = mutableListOf<StageJsonReport>()
        step.getTestStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        return StageJsonReport(
            id = step.id,
            name = step.title,
            type = "step",
            execution = mapExecutionReport(execution),
            attachments = mapAttachments(step.getAttachments()),
            stages = nestedStages,
        )
    }

    private fun buildScenarioReport(scenario: InstrumentedScenarioReport): StageJsonReport {
        val nestedStages = mutableListOf<StageJsonReport>()
        scenario.getTestStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        return StageJsonReport(
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
    ): StageJsonReport {
        val nestedStages = mutableListOf<StageJsonReport>()
        beforeAfterReport.getTestStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        val executionMetadata = beforeAfterReport.getExecutionMetadata()
        return StageJsonReport(
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

@Serializable
internal data class CariocaJsonTestReport(
    val testId: String,
    val testDescription: String,
    val testClass: String,
    val testName: String,
    val testFullName: String,
    val execution: ExecutionJsonReport,
    val beforeStages: List<StageJsonReport>,
    val stages: List<StageJsonReport>,
    val afterStages: List<StageJsonReport>,
    val attachments: List<AttachmentJsonReport>,
)

@Serializable
internal data class CariocaJsonSuiteReport(
    val packageName: String,
    val execution: ExecutionJsonReport,
    val testStatus: List<SuiteTestStatus>,
)

@Serializable
internal data class SuiteTestStatus(
    val status: String,
    val total: Int,
)

@Serializable
internal data class StageJsonReport(
    val id: String,
    val name: String,
    val type: String,
    val stages: List<StageJsonReport>,
    val execution: ExecutionJsonReport,
    val attachments: List<AttachmentJsonReport>,
)

@Serializable
internal data class ExecutionJsonReport(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val status: String,
    val failureMessage: String?,
    val failureStacktrace: String?,
)

@Serializable
internal data class AttachmentJsonReport(
    val description: String,
    val path: String,
    val mimeType: String,
)
