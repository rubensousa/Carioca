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

import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.android.report.suite.TestSuiteReport
import com.rubensousa.carioca.junit4.report.TestMetadata
import java.io.OutputStream

/**
 * Implement this to generate your own test reports, in any format you want
 */
interface CariocaInstrumentedReporter {

    /**
     * @param metadata the test report to be saved
     *
     * @return The relative output directory for this report
     */
    fun getOutputDir(metadata: TestMetadata): String

    /**
     * @param test test report to be written
     * @param storageProvider the storage provider to query for a [OutputStream]
     */
    fun writeTestReport(
        test: InstrumentedTestReport,
        storageProvider: ReportStorageProvider,
    )

    /**
     * @param report suite report to be written
     * @param outputStream the destination of the report contents
     */
    fun writeSuiteReport(
        report: TestSuiteReport,
        storageProvider: ReportStorageProvider,
    )

    /**
     * @return the filename of the screenshot, excluding the extension
     */
    fun getScreenshotName(id: String): String = id

    /**
     * @return the filename of the recording, excluding the extension
     */
    fun getRecordingName(id: String): String = id

}
