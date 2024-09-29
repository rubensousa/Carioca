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

package com.rubensousa.carioca.report.android

import com.rubensousa.carioca.report.android.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.android.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.report.android.interceptor.LoggerInterceptor
import com.rubensousa.carioca.report.android.interceptor.TakeScreenshotOnFailureInterceptor
import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.android.stage.InstrumentedTestScope
import com.rubensousa.carioca.report.android.stage.internal.InstrumentedBlockingTest
import com.rubensousa.carioca.report.android.stage.internal.InstrumentedBlockingTestBuilder
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.android.storage.TestStorageProvider
import com.rubensousa.carioca.report.runtime.TestMetadata
import com.rubensousa.carioca.report.runtime.TestReportConfig


/**
 * A test rule that builds a detailed report for a test, including its steps.
 *
 * You can also extend this class to provide a default report configuration across all tests:
 *
 * ```kotlin
 * class SampleReportRule : InstrumentedReportRule(
 *     reporter = CariocaAllureReporter(),
 *     recordingOptions = RecordingOptions(
 *         bitrate = 20_000_000,
 *         resolutionScale = 1.0f,
 *     ),
 *     screenshotOptions = ScreenshotOptions(
 *         scale = 1f
 *     ),
 *     interceptors = listOf(LoggerInterceptor(), DumpHierarchyInterceptor())
 * )
 * ```
 *
 * ```kotlin
 *
 * @get:Rule
 * val report = SampleReportRule()
 *
 * @Test
 * fun sampleTest() = report {
 *     step("Open notification and quick settings") {
 *         step("Open notification") {
 *             device.openNotification()
 *             screenshot("Notification bar visible")
 *         }
 *         step("Open quick settings") {
 *             device.openQuickSettings()
 *             screenshot("Quick settings displayed")
 *         }
 *     }
 *     step("Press home") {
 *         device.pressHome()
 *     }
 * }
 * ```
 *
 * @param reporter the instrumented reporter for this report
 * @param recordingOptions the default recording options for this report
 * @param screenshotOptions the default screenshot options for this report
 * @param interceptors the interceptors that will receive multiple stage events
 * during the lifecycle of this report
 */
open class InstrumentedReportRule internal constructor(
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

    private val testBuilder = InstrumentedBlockingTestBuilder(storageProvider)

    /**
     * Public constructor that uses [TestStorageProvider] to save the reports
     */
    constructor(
        reporter: InstrumentedReporter = DefaultInstrumentedReporter(),
        recordingOptions: RecordingOptions = RecordingOptions(),
        screenshotOptions: ScreenshotOptions = ScreenshotOptions(),
        interceptors: List<CariocaInstrumentedInterceptor> = listOf(
            LoggerInterceptor(),
            TakeScreenshotOnFailureInterceptor(),
            DumpViewHierarchyInterceptor(),
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
        return testBuilder.build(
            reportConfig = reportConfig,
            testMetadata = testMetadata,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            reporter = reporter,
            interceptors = interceptors
        )
    }

    /**
     * Same as [test], but without the extra method call
     */
    operator fun invoke(block: InstrumentedTestScope.() -> Unit) {
        test(block)
    }

    /**
     * Use this to start reporting the main test body
     */
    fun test(block: InstrumentedTestScope.() -> Unit) {
        executeTestAction {
            block()
        }
    }

    /**
     * Use this to track `@Before` methods separately from the other stages
     */
    fun before(title: String = "Before", block: InstrumentedStageScope.() -> Unit) {
        executeTestAction {
            before(title, block)
        }
    }

    /**
     * Use this to track `@After` methods separately from the other stages
     */
    fun after(title: String = "After", block: InstrumentedStageScope.() -> Unit) {
        executeTestAction {
            after(title, block)
        }
    }

    private fun executeTestAction(
        action: InstrumentedBlockingTest.() -> Unit,
    ) {
        val currentTest = getCurrentTest<InstrumentedBlockingTest>()
        try {
            action(currentTest)
        } catch (error: Throwable) {
            currentTest.onFailed(error)
            throw error
        }
    }

}
