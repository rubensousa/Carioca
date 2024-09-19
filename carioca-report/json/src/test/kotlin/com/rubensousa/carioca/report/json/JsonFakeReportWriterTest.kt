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
import com.rubensousa.carioca.report.runtime.StageAttachment
import com.rubensousa.carioca.report.runtime.StageReport
import com.rubensousa.carioca.report.runtime.TestMetadata
import org.junit.After
import org.junit.Test
import java.io.File

class JsonFakeReportWriterTest {

    private val currentDir = File(".").absoluteFile.parentFile
    private val reportDir = File(currentDir, "carioca-report").also {
        it.mkdirs()
    }
    private val outputFile = File(reportDir, "dummy_test_report.json")

    @After
    fun deleteOutputFile() {
        reportDir.deleteRecursively()
    }

    @Test
    fun `report is saved correctly`() {
        // given
        val writer = JsonReportWriter()
        val parser = JsonReportParser()
        val metadata = TestMetadata(
            packageName = "package",
            className = "class",
            methodName = "method"
        )
        val report = FakeReport()
        report.ignore()
        report.setParameter("key", "value")

        // when
        writer.writeReport(
            metadata = metadata,
            testReport = report,
            file = outputFile
        )

        // then
        val parsedReport = parser.parseTestReports(reportDir).first().report
        assertThat(parsedReport.fullName).isEqualTo(metadata.fullName)
        assertThat(parsedReport.beforeStages).isEmpty()
        assertThat(parsedReport.stages).isEmpty()
        assertThat(parsedReport.afterStages).isEmpty()
        assertThat(parsedReport.attachments).isEmpty()
        assertThat(parsedReport.parameters.first().key).isEqualTo("key")
        assertThat(parsedReport.parameters.first().value).isEqualTo("value")

    }

    class FakeReport : StageReport() {

        override fun deleteAttachment(attachment: StageAttachment) {
            // No-op
        }

        override fun getType(): String = "Type"

        override fun getTitle(): String = "Title"
    }

}
