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

package com.rubensousa.carioca.report.android.coroutines

import com.rubensousa.carioca.report.android.AbstractInstrumentedReportRule
import com.rubensousa.carioca.report.android.DefaultInstrumentedReporter
import com.rubensousa.carioca.report.android.InstrumentedReportRule
import com.rubensousa.carioca.report.android.InstrumentedReporter
import com.rubensousa.carioca.report.android.coroutines.internal.InstrumentedCoroutineTest
import com.rubensousa.carioca.report.android.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.android.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.report.android.interceptor.LoggerInterceptor
import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.android.storage.TestStorageProvider
import com.rubensousa.carioca.report.runtime.TestMetadata
import com.rubensousa.carioca.report.runtime.TestReportConfig
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A report rule for coroutine tests.
 * Please check [InstrumentedReportRule] for more info on how to use the basic report functionality
 */
open class InstrumentedCoroutineReportRule internal constructor(
    reporter: InstrumentedReporter,
    recordingOptions: RecordingOptions,
    screenshotOptions: ScreenshotOptions,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
    private val storageProvider: ReportStorageProvider,
) : AbstractInstrumentedReportRule(
    reporter = reporter,
    recordingOptions = recordingOptions,
    screenshotOptions = screenshotOptions
) {

    /**
     * Public constructor that uses [TestStorageProvider] to save the reports
     */
    constructor(
        reporter: InstrumentedReporter = DefaultInstrumentedReporter(),
        recordingOptions: RecordingOptions = RecordingOptions(),
        screenshotOptions: ScreenshotOptions = ScreenshotOptions(),
        interceptors: List<CariocaInstrumentedInterceptor> = listOf(
            LoggerInterceptor(),
            DumpViewHierarchyInterceptor()
        ),
    ) : this(
        reporter = reporter,
        recordingOptions = recordingOptions,
        screenshotOptions = screenshotOptions,
        interceptors = interceptors,
        storageProvider = TestStorageProvider
    )

    override fun createTest(
        reportConfig: TestReportConfig?,
        testMetadata: TestMetadata,
        recordingOptions: RecordingOptions,
        screenshotOptions: ScreenshotOptions,
    ): InstrumentedTestReport {
        val outputPath = reporter.getOutputDir(testMetadata)
        val testReport = InstrumentedCoroutineTest(
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

    /**
     * Same as [runTest]
     */
    operator fun invoke(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend InstrumentedCoroutineTestScope.() -> Unit,
    ) {
        runTest(context, block)
    }

    /**
     * Runs the report inside a coroutine scope
     */
    fun runTest(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend InstrumentedCoroutineTestScope.() -> Unit,
    ) {
        kotlinx.coroutines.test.runTest(context) {
            block(getCurrentTest())
        }
    }

}
