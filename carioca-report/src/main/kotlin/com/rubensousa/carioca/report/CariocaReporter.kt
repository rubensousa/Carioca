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

import com.rubensousa.carioca.report.stage.TestReport
import java.io.OutputStream

/**
 * Implement this to generate your own test reports, in any format you want,
 * or use [CariocaJsonReporter] for a template json file
 */
interface CariocaReporter {

    /**
     * @param report the test report to be saved
     *
     * @return The relative output directory for this report
     */
    fun getOutputDir(report: TestReport): String

    /**
     * @param report the test report to be saved
     *
     * @return the filename of the report, including the extension
     */
    fun getReportFilename(report: TestReport): String

    /**
     * @param report test report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeTestReport(report: TestReport, outputStream: OutputStream)

    /**
     * @return the filename of the screenshot, excluding the extension
     */
    fun getScreenshotName(id: String): String = id

    /**
     * @return the filename of the recording, excluding the extension
     */
    fun getRecordingName(id: String): String = id

}
