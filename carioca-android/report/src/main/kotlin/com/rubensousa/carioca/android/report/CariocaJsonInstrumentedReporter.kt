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

import com.rubensousa.carioca.android.report.stage.scenario.InstrumentedScenarioStage
import com.rubensousa.carioca.android.report.stage.step.InstrumentedStepStage
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTestStage
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import com.rubensousa.carioca.stage.CariocaStage
import com.rubensousa.carioca.stage.ExecutionMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream
import java.io.OutputStream

/**
 * Represents the test report in a json format that can be used for analysing the test output
 */
class CariocaJsonInstrumentedReporter : CariocaInstrumentedReporter {

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        explicitNulls = false
    }

    override fun getOutputDir(stage: InstrumentedTestStage): String {
        val metadata = stage.getMetadata()
        return "${metadata.className}/${metadata.methodName}"
    }

    override fun getReportFilename(stage: InstrumentedTestStage): String {
        val metadata = stage.getExecutionMetadata()
        return "${metadata.uniqueId}_report.json"
    }

    override fun getSuiteReportFilePath(report: TestSuiteReport): String {
        return "suite_report.json"
    }

    @ExperimentalSerializationApi
    override fun writeTestReport(stage: InstrumentedTestStage, outputStream: OutputStream) {
        val jsonReport = buildTestReport(stage)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    @ExperimentalSerializationApi
    override fun writeSuiteReport(report: TestSuiteReport, outputStream: OutputStream) {
        val jsonReport = buildSuiteReport(report)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    private fun buildTestReport(report: InstrumentedTestStage): CariocaJsonTestReport {
        val metadata = report.getMetadata()
        return CariocaJsonTestReport(
            testId = metadata.testId,
            testDescription = metadata.testTitle,
            testClass = metadata.className,
            testName = metadata.methodName,
            testFullName = metadata.getTestFullName(),
            execution = mapExecutionReport(report.getExecutionMetadata()),
            attachments = mapAttachments(report.getAttachments()),
            stages = report.getStages().mapNotNull { stage ->
                buildStageReport(stage)
            }
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

    private fun buildStageReport(report: CariocaStage): StageJsonReport? {
        return when (report) {
            is InstrumentedStepStage -> buildStepReport(report)
            is InstrumentedScenarioStage -> buildScenarioReport(report)
            else -> null
        }
    }

    private fun mapAttachments(attachments: List<ReportAttachment>): List<AttachmentJsonReport> {
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

    private fun buildStepReport(step: InstrumentedStepStage): StageJsonReport {
        val metadata = step.getMetadata()
        val execution = step.getExecutionMetadata()
        val nestedStages = mutableListOf<StageJsonReport>()
        step.getStages().forEach { nestedStage ->
            buildStageReport(nestedStage)?.let { nestedStages.add(it) }
        }
        return StageJsonReport(
            name = metadata.title,
            type = "step",
            execution = mapExecutionReport(execution),
            attachments = mapAttachments(step.getAttachments()),
            stages = nestedStages,
        )
    }

    private fun buildScenarioReport(scenario: InstrumentedScenarioStage): StageJsonReport {
        val nestedSteps = mutableListOf<StageJsonReport>()
        scenario.getSteps().forEach { nestedStep ->
            nestedSteps.add(buildStepReport(nestedStep))
        }
        return StageJsonReport(
            name = scenario.getMetadata().name,
            type = "scenario",
            execution = mapExecutionReport(scenario.getExecutionMetadata()),
            attachments = emptyList(),
            stages = nestedSteps,
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
    val stages: List<StageJsonReport>,
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
