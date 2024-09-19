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
import com.rubensousa.carioca.report.json.JsonReportFiles
import com.rubensousa.carioca.report.json.JsonReportWriter
import com.rubensousa.carioca.report.runtime.TestMetadata

/**
 * A template reporter that uses [JsonReportWriter] to save the reports to the test storage
 */
class DefaultInstrumentedReporter : CariocaInstrumentedReporter {

    private val reportWriter = JsonReportWriter()

    override fun getOutputDir(metadata: TestMetadata): String {
        return JsonReportFiles.REPORT_DIR
    }

    override fun writeTestReport(
        test: InstrumentedTestReport,
        storageProvider: ReportStorageProvider,
    ) {
        val metadata = test.getExecutionMetadata()
        val filePath = "${test.outputPath}/${metadata.uniqueId}_test_report.json"
        val outputStream = storageProvider.getOutputStream(filePath)
        reportWriter.writeReport(test.metadata, test, outputStream)
    }

}
