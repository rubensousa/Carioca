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

package com.rubensousa.carioca.report.serialization

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class JsonReportParserTest {

    @Test
    fun `report directory is found`() {
        // given
        val parser = JsonReportParser()

        // when
        var currentDir = findTestDir()
        while (parser.findReportDir(currentDir) == null) {
            currentDir = File(currentDir.parent)
        }

        // then
        val reportDir = parser.findReportDir(currentDir)!!
        assertThat(reportDir.name).isEqualTo(JsonReportFiles.REPORT_DIR)
    }
    
    @Test
    fun `report contains content from json`() {
        // given
        val parser = JsonReportParser()
        val testDir = findTestDir()

        // when
        val testReports = parser.parseTestReports(testDir)

        // then
        assertThat(testReports).hasSize(1)
        val testReport = testReports.first()
        assertThat(testReport.file.name).isEqualTo("complete_test_report.json")
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
