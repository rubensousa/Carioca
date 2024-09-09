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

package com.rubensousa.carioca.report.internal

import android.util.Log
import com.rubensousa.carioca.report.stage.TestReport

internal object TestReportWriter {

    fun write(report: TestReport) {
        val reporter = report.reporter
        val dir = TestStorageProvider.getTestOutputDir(report, reporter)
        val file = "$dir/${reporter.getReportFilename(report)}"
        val outputStream = TestStorageProvider.getOutputStream(file)
        try {
            reporter.writeTestReport(report, outputStream)
        } catch (exception: Exception) {
            Log.e("CariocaReport", "Failed writing report for test ${report.name}", exception)
        } finally {
            outputStream.close()
        }
    }

}
