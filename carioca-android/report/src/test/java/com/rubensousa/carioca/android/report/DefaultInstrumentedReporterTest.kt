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

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.android.report.fake.FakeReportStorageProvider
import com.rubensousa.carioca.android.report.fake.FakeStageReport
import com.rubensousa.carioca.report.json.JsonReportFiles
import com.rubensousa.carioca.report.json.JsonReportParser
import com.rubensousa.carioca.report.runtime.TestMetadata
import org.junit.Rule
import org.junit.Test

class DefaultInstrumentedReporterTest {

    @get:Rule
    val temporaryStorageRule = TemporaryStorageRule()

    private val metadata = TestMetadata(
        packageName = "package",
        className = "class",
        methodName = "method"
    )
    private val fakeStorageProvider = FakeReportStorageProvider()
    private val parser = JsonReportParser()

    @Test
    fun `output dir is the same as json module`() {
        // given
        val reporter = DefaultInstrumentedReporter()

        // when
        val outputDir = reporter.getOutputDir(metadata)

        // then
        assertThat(outputDir).isEqualTo("/${JsonReportFiles.REPORT_DIR}")
    }

    @Test
    fun `report is saved to storage`() {
        // given
        val reporter = DefaultInstrumentedReporter()
        val report = FakeStageReport()
        report.pass()
        val expectedPath = "/${JsonReportFiles.REPORT_DIR}/${report.executionId}_${JsonReportFiles.TEST_REPORT}"

        // when
        reporter.writeTestReport(metadata, report, fakeStorageProvider)

        // then
        val file = fakeStorageProvider.filesSaved[expectedPath]
        assertThat(parser.parseTestReport(file!!)).isNotNull()
    }
}