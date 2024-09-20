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

package com.rubensousa.carioca.android.report.stage.internal

import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import com.rubensousa.carioca.junit4.report.getTestMetadata
import com.rubensousa.carioca.junit4.report.getTestReportConfig
import org.junit.runner.Description

internal class InstrumentedTestBuilder(
    private val storageProvider: ReportStorageProvider,
) {

    fun build(
        description: Description,
        recordingOptions: RecordingOptions,
        screenshotOptions: ScreenshotOptions,
        reporter: CariocaInstrumentedReporter,
        interceptors: List<CariocaInstrumentedInterceptor>,
    ): InstrumentedBlockingTest {
        val reportConfig = description.getTestReportConfig()
        val testMetadata = description.getTestMetadata()
        val outputPath = reporter.getOutputDir(testMetadata)
        val testReport = InstrumentedBlockingTest(
            outputPath = outputPath,
            metadata = testMetadata,
            recordingOptions = recordingOptions,
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
