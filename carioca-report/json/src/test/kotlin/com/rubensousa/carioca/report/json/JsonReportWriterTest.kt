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

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.report.runtime.ReportProperty
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageReport
import com.rubensousa.carioca.report.runtime.TestMetadata
import org.junit.After
import org.junit.Test
import java.io.File

class JsonReportWriterTest {

    private val currentDir = File(".").absoluteFile.parentFile
    private val reportDir = File(currentDir, "carioca-report").also {
        it.mkdirs()
    }
    private val outputFile = File(reportDir, "dummy_test_report.json")
    private val metadata = TestMetadata(
        packageName = "package",
        className = "class",
        methodName = "method"
    )
    private val writer = JsonReportWriter()

    @After
    fun deleteOutputFile() {
        reportDir.deleteRecursively()
    }

    @Test
    fun `report template is saved correctly`() {
        // given
        val report = FakeReport()
        val stageReport = FakeReport()
        stageReport.pass()
        stageReport.setParameter("stage", "stage_value")
        val nestedStage = FakeReport()
        nestedStage.pass()
        nestedStage.setParameter("nested_stage", "nested_value")
        stageReport.addTestStage(nestedStage)

        report.ignore()
        report.addProperty(ReportProperty.Description, "description")
        report.setParameter("key", "value")
        report.addStageBefore(stageReport)
        report.addTestStage(stageReport)
        report.addStageAfter(stageReport)

        // when
        writer.writeReport(
            metadata = metadata,
            testReport = report,
            file = outputFile
        )

        // then
        val expectedStageParams = JsonParameter("stage", "stage_value")
        val expectedNestedStageParams = JsonParameter("nested_stage", "nested_value")
        val parsedReport = parseWrittenReport()
        assertThat(parsedReport.description).isEqualTo("description")
        assertThat(parsedReport.fullName).isEqualTo(metadata.fullName)
        assertThat(parsedReport.attachments).isEmpty()
        assertThat(parsedReport.parameters.first().key).isEqualTo("key")
        assertThat(parsedReport.parameters.first().value).isEqualTo("value")
        assertThat(parsedReport.stages.first().stages.first().parameters.first())
            .isEqualTo(expectedNestedStageParams)
        assertThat(parsedReport.beforeStages.first().parameters.first())
            .isEqualTo(expectedStageParams)
        assertThat(parsedReport.stages.first().parameters.first())
            .isEqualTo(expectedStageParams)
        assertThat(parsedReport.afterStages.first().parameters.first())
            .isEqualTo(expectedStageParams)
    }

    @Test
    fun `nested stages are saved`() {
        // given
        val report = FakeReport()
        val nestedReport1 = FakeReport()
        val nestedReport2 = FakeReport()

        report.addTestStage(nestedReport1)
        nestedReport1.addTestStage(nestedReport2)

        nestedReport2.pass()
        nestedReport1.pass()
        report.pass()

        // when
        writer.writeReport(
            metadata = metadata,
            testReport = report,
            file = outputFile
        )

        // then
        val parsedReport = parseWrittenReport()
        val nestedStage = parsedReport.stages.first()
        val nestedStage2 = nestedStage.stages.first()
        assertThat(parsedReport.execution.id).isEqualTo(report.executionId)
        assertThat(nestedStage.execution.id).isEqualTo(nestedReport1.executionId)
        assertThat(nestedStage2.execution.id).isEqualTo(nestedReport2.executionId)

    }

    @Test
    fun `attachment relative path is resolved`() {
        // given
        val report = FakeReport()
        val attachment = StageAttachment(
            description = "path with leading report dir",
            path = "/${JsonReportFiles.REPORT_DIR}/image.jpg",
            mimeType = "image/jpg",
            keepOnSuccess = true
        )
        report.attach(attachment)
        report.pass()

        // when
        writer.writeReport(
            metadata = metadata,
            testReport = report,
            file = outputFile
        )

        // then
        val parsedReport = parseWrittenReport()
        assertThat(parsedReport.attachments.first()).isEqualTo(
            JsonAttachment(
                description = attachment.description,
                path = "image.jpg",
                mimeType = attachment.mimeType
            )
        )
    }

    @Test
    fun `failed report is saved correctly`() {
        // given
        val report = FakeReport()
        val cause = IllegalStateException("Failed")
        report.fail(cause)

        // when
        writer.writeReport(
            metadata = metadata,
            testReport = report,
            file = outputFile
        )

        // then
        val parsedReport = parseWrittenReport()
        assertThat(parsedReport.execution.status).isEqualTo(JsonExecutionStatus.FAILED)
        assertThat(parsedReport.execution.failureMessage).isEqualTo("Failed")
        assertThat(parsedReport.execution.failureStacktrace).isEqualTo(cause.stackTraceToString())
    }

    @Test
    fun `links are saved`() {
        // given
        val report = FakeReport()
        val links = listOf("link")
        report.addProperty(ReportProperty.Links, links)
        report.pass()

        // when
        writer.writeReport(
            metadata = metadata,
            testReport = report,
            file = outputFile
        )

        // then
        val parsedReport = parseWrittenReport()
        assertThat(parsedReport.links).isEqualTo(links)
    }

    private fun parseWrittenReport(): JsonTestReport {
        return JsonReportParser().parseTestReports(reportDir).first().report
    }

    class FakeReport : StageReport() {

        override fun deleteAttachment(attachment: StageAttachment) {
            // No-op
        }

        override fun getType(): String = "Type"

        override fun getTitle(): String = "Title"
    }

}
