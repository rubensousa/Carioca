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
import org.junit.Test
import java.io.File

class JsonFakeReportParserTest {

    private val parser = JsonReportParser()

    @Test
    fun `report directory is found`() {
        // given
        var currentDir = findTestDir()

        // when
        while (parser.findReportDir(currentDir) == null) {
            currentDir = File(currentDir.parent)
        }

        // then
        val reportDir = parser.findReportDir(currentDir)!!
        assertThat(reportDir.name).isEqualTo(JsonReportFiles.REPORT_DIR)
    }

    @Test
    fun `invalid report files are excluded`() {
        // given
        val currentDir = findTestDir()

        // when
        val testReports = parser.parseTestReports(currentDir)
        val testReport = testReports.find { it.file.name == "wrong_format_test_report.json" }

        // then
        assertThat(testReports.size).isEqualTo(3)
        assertThat(testReport).isNull()
    }

    @Test
    fun `successful report contains content from json`() {
        // given
        val testDir = findTestDir()

        // when
        val testReports = parser.parseTestReports(testDir)

        // then
        val testReport = testReports.find { it.file.name == "success_test_report.json" }!!
        val report = testReport.report

        /**
         * Check report test properties
         */
        assertThat(report.id)
            .isEqualTo("com.rubensousa.carioca.android.report.sample.test.SampleRecordingTest.testRecordingOverride")
        assertThat(report.title).isEqualTo("testRecordingOverride")
        assertThat(report.description).isEqualTo("some description")
        assertThat(report.packageName).isEqualTo("com.rubensousa.carioca.android.report.sample.test")
        assertThat(report.className).isEqualTo("SampleRecordingTest")
        assertThat(report.methodName).isEqualTo("testRecordingOverride")
        assertThat(report.fullName).isEqualTo("com.rubensousa.carioca.android.report.sample.test.SampleRecordingTest.testRecordingOverride")
        assertThat(report.links).isEqualTo(listOf("https://blabla.com"))
        with(report.execution) {
            assertThat(id).isEqualTo("fb3b36d0-24b3-41aa-9612-09b447eaf79a")
            assertThat(startTime).isEqualTo(1726706583788L)
            assertThat(endTime).isEqualTo(1726706591719L)
            assertThat(status).isEqualTo(JsonExecutionStatus.PASSED)
            assertThat(failureMessage).isNull()
            assertThat(failureStacktrace).isNull()
        }
        assertThat(report.attachments).isEqualTo(
            listOf(
                JsonAttachment(
                    description = "Screen recording",
                    path = "f615abd4-5679-4179-a633-7be8a06cd85b.mp4",
                    mimeType = "video/mp4"
                )
            )
        )

        /**
         * Check before stage
         */
        with(report.beforeStages.first()) {
            assertThat(id).isEqualTo("6f3590e8-acc3-4fe5-8c73-52558015afd1")
            assertThat(name).isEqualTo("Do something before")
            assertThat(type).isEqualTo("Before")
            assertThat(execution.id).isEqualTo("6f3590e8-acc3-4fe5-8c73-52558015afd1")
            assertThat(execution.startTime).isEqualTo(1726706585850L)
            assertThat(execution.endTime).isEqualTo(1726706586527L)
            assertThat(execution.status).isEqualTo(JsonExecutionStatus.PASSED)
            assertThat(attachments).isEmpty()
            with(stages.first()) {
                assertThat(id).isEqualTo("692eb765-5291-4159-aad5-13dc539488b0")
                assertThat(name).isEqualTo("Press home")
                assertThat(type).isEqualTo("Step")
                assertThat(stages).isEmpty()
                assertThat(execution.id).isEqualTo("e286cff3-e4b8-4224-a3dd-92ff623f7443")
                assertThat(execution.startTime).isEqualTo(1726706585853L)
                assertThat(execution.endTime).isEqualTo(1726706585924L)
                assertThat(execution.status).isEqualTo(JsonExecutionStatus.PASSED)
                assertThat(attachments).isEqualTo(
                    listOf(
                        JsonAttachment(
                            description = "Notifications opened",
                            path = "08bd21fa-1d3e-8f0f-4714-5e28b1213d42.jpg",
                            mimeType = "image/jpg"
                        )
                    )
                )
            }
        }

        /**
         * Check test stages
         */
        with(report.stages.first()) {
            assertThat(id).isEqualTo("a914273c-3560-4baa-97de-244e6b5bd690")
            assertThat(name).isEqualTo("Given: User opens notifications")
            assertThat(type).isEqualTo("Step")
            assertThat(stages).isEmpty()
            assertThat(execution.id).isEqualTo("0b97ebb0-ae00-4943-b4ec-68a1e0c5f114")
            assertThat(execution.startTime).isEqualTo(1726706586529L)
            assertThat(execution.endTime).isEqualTo(1726706587863L)
            assertThat(execution.status).isEqualTo(JsonExecutionStatus.PASSED)
            assertThat(attachments).isEqualTo(
                listOf(
                    JsonAttachment(
                        description = "Notifications closed",
                        path = "1d3easdas-3213-4714-8f0f-5e28b1213d42.jpg",
                        mimeType = "image/jpg"
                    )
                )
            )
        }

        /**
         * Check after stages
         */
        assertThat(report.afterStages).hasSize(1)
        with(report.afterStages.first()) {
            assertThat(id).isEqualTo("cc7f06b5-a5ed-4d35-b35a-80d7447f7bbf")
            assertThat(name).isEqualTo("Do something after")
            assertThat(type).isEqualTo("After")
            assertThat(execution.id).isEqualTo("cc7f06b5-a5ed-4d35-b35a-80d7447f7bbf")
            assertThat(execution.startTime).isEqualTo(1726706588107)
            assertThat(execution.endTime).isEqualTo(1726706589063)
            assertThat(execution.status).isEqualTo(JsonExecutionStatus.PASSED)
            assertThat(attachments).isEmpty()
            assertThat(stages).hasSize(1)
            with(stages.first()) {
                assertThat(id).isEqualTo("6cd999f3-6424-45f6-9133-b1f4262c0a9f")
                assertThat(name).isEqualTo("Press home")
                assertThat(type).isEqualTo("Step")
                assertThat(stages).isEmpty()
                assertThat(execution.id).isEqualTo("aa762253-a47c-4167-abf3-e4af8ad0752b")
                assertThat(execution.startTime).isEqualTo(1726706588108)
                assertThat(execution.endTime).isEqualTo(1726706589057)
                assertThat(execution.status).isEqualTo(JsonExecutionStatus.PASSED)
                assertThat(attachments).isEqualTo(
                    listOf(
                        JsonAttachment(
                            description = "Pressed home",
                            path = "asdgf21fa-1d3e-4714-8f0f-5e28b1213d42.jpg",
                            mimeType = "image/jpg"
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `error report contains content from json`() {
        // given
        val parser = JsonReportParser()
        val testDir = findTestDir()
        val stacktrace = "java.lang.IllegalStateException: Failed"
        val message = "Failed"

        // when
        val testReports = parser.parseTestReports(testDir)

        // then
        val testReport = testReports.find { it.file.name == "fail_test_report.json" }!!
        val report = testReport.report

        /**
         * Check report test properties
         */
        with(report.execution) {
            assertThat(id).isEqualTo("071b33c1-4596-4cda-a7bd-caaa217cff8c")
            assertThat(status).isEqualTo(JsonExecutionStatus.FAILED)
            assertThat(failureMessage).isEqualTo(message)
            assertThat(failureStacktrace).isEqualTo(stacktrace)
        }

        /**
         * Check test stages
         */
        with(report.stages.first()) {
            assertThat(execution.id).isEqualTo("cb4794e0-985f-4d44-ae35-2ae5c81635e7")
            assertThat(execution.status).isEqualTo(JsonExecutionStatus.FAILED)
            assertThat(execution.failureMessage).isEqualTo(message)
            assertThat(execution.failureStacktrace).isEqualTo(stacktrace)
            assertThat(attachments).isEmpty()
        }

    }

    @Test
    fun `parsing ignored report`() {
        // given
        val testDir = findTestDir()

        // when
        val testReports = parser.parseTestReports(testDir)

        // then
        val testReport = testReports.find { it.file.name == "ignored_test_report.json" }!!
        val report = testReport.report

        with(report.execution) {
            assertThat(id).isEqualTo("0d0ab6a2-765a-426a-8315-56415c1da04d")
            assertThat(status).isEqualTo(JsonExecutionStatus.IGNORED)
        }
    }


    private fun findTestDir(): File {
        var currentFile = File("anchor").absoluteFile
        // Go up until we find the test directory
        while (currentFile.name != "json") {
            currentFile = currentFile.parentFile
        }
        return currentFile
    }

}
