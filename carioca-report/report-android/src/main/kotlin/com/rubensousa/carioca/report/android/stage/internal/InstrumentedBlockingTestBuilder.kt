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

package com.rubensousa.carioca.report.android.stage.internal

import com.rubensousa.carioca.report.android.InstrumentedReporter
import com.rubensousa.carioca.report.android.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.android.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.runtime.TestMetadata
import com.rubensousa.carioca.report.runtime.TestReportConfig

internal class InstrumentedBlockingTestBuilder(
    private val storageProvider: ReportStorageProvider,
) {

    fun build(
        reportConfig: TestReportConfig?,
        testMetadata: TestMetadata,
        screenshotOptions: ScreenshotOptions,
        reporter: InstrumentedReporter,
        interceptors: List<CariocaInstrumentedInterceptor>,
    ): InstrumentedBlockingTest {
        val outputPath = reporter.getOutputDir(testMetadata)
        val testReport = InstrumentedBlockingTest(
            outputPath = outputPath,
            metadata = testMetadata,
            screenshotDelegate = ScreenshotDelegate(
                outputPath = outputPath,
                defaultOptions = screenshotOptions,
                storageProvider = storageProvider
            ),
            interceptors = interceptors,
            reporter = reporter,
            storageProvider = storageProvider
        )
        reportConfig?.applyTo(testReport)
        return testReport
    }

}
