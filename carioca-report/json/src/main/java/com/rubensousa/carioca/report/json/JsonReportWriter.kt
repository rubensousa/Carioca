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

package com.rubensousa.carioca.report.json

import com.rubensousa.carioca.report.runtime.ExecutionMetadata
import com.rubensousa.carioca.report.runtime.ReportProperty
import com.rubensousa.carioca.report.runtime.ReportStatus
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageReport
import com.rubensousa.carioca.report.runtime.TestMetadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A reporter that saves the test results in a json format.
 *
 * Use [writeReport] to save the report to a file or output stream
 */
class JsonReportWriter {

    @ExperimentalSerializationApi
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        explicitNulls = false
    }

    fun writeReport(
        metadata: TestMetadata,
        testReport: StageReport,
        file: File,
    ) {
        val outputStream = FileOutputStream(file)
        writeReport(metadata, testReport, outputStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun writeReport(
        metadata: TestMetadata,
        testReport: StageReport,
        outputStream: OutputStream,
    ) {
        val jsonReport = buildTestReport(metadata, testReport)
        BufferedOutputStream(outputStream).use { stream ->
            json.encodeToStream(jsonReport, stream)
            stream.flush()
        }
    }

    private fun buildTestReport(metadata: TestMetadata, test: StageReport): JsonTestReport {
        return JsonTestReport(
            id = test.getId(),
            title = test.getTitle(),
            description = test.getProperty(ReportProperty.Description),
            packageName = metadata.packageName,
            className = metadata.className,
            methodName = metadata.methodName,
            fullName = metadata.fullName,
            links = test.getProperty<List<String>>(ReportProperty.Links).orEmpty(),
            execution = mapExecutionReport(test.getExecutionMetadata()),
            attachments = mapAttachments(test.getAttachments()),
            beforeStages = test.getStagesBefore().map { stage ->
                buildStageReport(stage)
            },
            stages = test.getTestStages().map { stage ->
                buildStageReport(stage)
            },
            afterStages = test.getStagesAfter().map { stage ->
                buildStageReport(stage)
            },
            parameters = mapParameters(test.getParameters())
        )
    }

    private fun buildStageReport(stage: StageReport): JsonStage {
        val execution = stage.getExecutionMetadata()
        val nestedStages = mutableListOf<JsonStage>()
        stage.getTestStages().forEach { nestedStage ->
            nestedStages.add(buildStageReport(nestedStage))
        }
        return JsonStage(
            id = stage.getId(),
            name = stage.getTitle(),
            type = stage.getType(),
            execution = mapExecutionReport(execution),
            attachments = mapAttachments(stage.getAttachments()),
            stages = nestedStages,
            parameters = mapParameters(stage.getParameters())
        )
    }

    private fun mapParameters(parameters: Map<String, String>): List<JsonParameter> {
        return parameters.map { entry ->
            JsonParameter(
                key = entry.key,
                value = entry.value
            )
        }
    }

    private fun mapAttachments(attachments: List<StageAttachment>): List<JsonAttachment> {
        return attachments.map { attachment ->
            JsonAttachment(
                description = attachment.description,
                path = getAttachmentPath(attachment.path),
                mimeType = attachment.mimeType
            )
        }
    }

    // Ensures that all the attachments paths are relative to the same directory
    private fun getAttachmentPath(path: String): String {
        return path.replace("/${JsonReportFiles.REPORT_DIR}/", "")
    }

    private fun mapExecutionReport(execution: ExecutionMetadata): JsonExecutionReport {
        return JsonExecutionReport(
            id = execution.uniqueId,
            status = when (execution.status) {
                ReportStatus.PASSED -> JsonExecutionStatus.PASSED
                ReportStatus.FAILED -> JsonExecutionStatus.FAILED
                else -> JsonExecutionStatus.IGNORED
            },
            startTime = execution.startTime,
            endTime = execution.endTime,
            failureStacktrace = execution.failureCause?.stackTraceToString(),
            failureMessage = execution.failureCause?.message
        )
    }

}
