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

import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.report.json.JsonReportFiles
import com.rubensousa.carioca.report.json.JsonReportWriter
import com.rubensousa.carioca.report.runtime.StageReport
import com.rubensousa.carioca.report.runtime.TestMetadata

/**
 * A template reporter that uses [JsonReportWriter] to save the reports to the test storage
 */
class DefaultInstrumentedReporter internal constructor(
    private val writer: JsonReportWriter,
) : InstrumentedReporter {

    constructor() : this(JsonReportWriter())

    override fun getOutputDir(metadata: TestMetadata): String {
        return "/${JsonReportFiles.REPORT_DIR}"
    }

    override fun writeTestReport(
        testMetadata: TestMetadata,
        report: StageReport,
        storageProvider: ReportStorageProvider,
    ): Result<Unit> {
        val executionMetadata = report.getExecutionMetadata()
        val dir = getOutputDir(testMetadata)
        val filePath = "${dir}/${executionMetadata.uniqueId}_${JsonReportFiles.TEST_REPORT}"
        val outputStream = storageProvider.getOutputStream(filePath)
        return writer.writeReport(testMetadata, report, outputStream)
    }

}
