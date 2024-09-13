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

import com.rubensousa.carioca.android.report.stage.test.InstrumentedTest
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTestMetadata
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import java.io.OutputStream

/**
 * Implement this to generate your own test reports, in any format you want,
 * or use [CariocaJsonInstrumentedReporter] for a template json file
 */
interface CariocaInstrumentedReporter {

    /**
     * @param test the test report to be saved
     *
     * @return The relative output directory for this report
     */
    fun getOutputDir(metadata: InstrumentedTestMetadata): String

    /**
     * @param stage the test report to be saved
     *
     * @return the filename of the report, including the extension
     */
    fun getReportFilename(test: InstrumentedTest): String

    /**
     * @param report the test report to be saved
     *
     * @return the relative path of the report, including the extension
     */
    fun getSuiteReportFilePath(report: TestSuiteReport): String

    /**
     * @param test test report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeTestReport(test: InstrumentedTest, outputStream: OutputStream)

    /**
     * @param report suite report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeSuiteReport(report: TestSuiteReport, outputStream: OutputStream)

    /**
     * @return the filename of the screenshot, excluding the extension
     */
    fun getScreenshotName(id: String): String = id

    /**
     * @return the filename of the recording, excluding the extension
     */
    fun getRecordingName(id: String): String = id

}
