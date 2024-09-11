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

package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.ExecutionMetadata
import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StageReport
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream
import java.io.OutputStream

/**
 * Represents the test report in a json format that can be used for analysing the test output
 */
class CariocaJsonReporter : CariocaReporter {

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        explicitNulls = false
    }

    override fun getOutputDir(report: TestReport): String {
        val metadata = report.getMetadata()
        return "${metadata.className}/${metadata.methodName}"
    }

    override fun getReportFilename(report: TestReport): String {
        val metadata = report.getMetadata().execution
        return "${metadata.uniqueId}_report.json"
    }

    @ExperimentalSerializationApi
    override fun writeTestReport(report: TestReport, outputStream: OutputStream) {
        val jsonReport = buildJsonReport(report)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    private fun buildJsonReport(report: TestReport): CariocaJsonReport {
        val metadata = report.getMetadata()
        return CariocaJsonReport(
            testId = metadata.testId,
            testDescription = metadata.testTitle,
            testClass = metadata.className,
            testName = metadata.methodName,
            testFullName = metadata.getTestFullName(),
            execution = mapExecutionReport(metadata.execution),
            attachments = mapAttachments(report.getAttachments()),
            stages = report.getStageReports().mapNotNull { stage ->
                buildStageReport(stage)
            }
        )
    }

    private fun buildStageReport(report: StageReport): StageJsonReport? {
        return when (report) {
            is StepReport -> buildStepReport(report)
            is ScenarioReport -> buildScenarioReport(report)
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

    private fun buildStepReport(step: StepReport): StageJsonReport {
        val metadata = step.getMetadata()
        val nestedSteps = mutableListOf<StageJsonReport>()
        step.getSteps().forEach { nestedStep ->
            nestedSteps.add(buildStepReport(nestedStep))
        }
        return StageJsonReport(
            name = metadata.title,
            type = "step",
            execution = mapExecutionReport(metadata.execution),
            attachments = mapAttachments(step.getAttachments()),
            steps = nestedSteps,
        )
    }

    private fun buildScenarioReport(scenario: ScenarioReport): StageJsonReport {
        val metadata = scenario.getMetadata()
        val nestedSteps = mutableListOf<StageJsonReport>()
        scenario.getSteps().forEach { nestedStep ->
            nestedSteps.add(buildStepReport(nestedStep))
        }
        return StageJsonReport(
            name = scenario.getMetadata().name,
            type = "scenario",
            execution = mapExecutionReport(metadata.execution),
            attachments = emptyList(),
            steps = nestedSteps,
        )
    }

}

@Serializable
internal data class CariocaJsonReport(
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
internal data class StageJsonReport(
    val name: String,
    val type: String,
    val steps: List<StageJsonReport>,
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
