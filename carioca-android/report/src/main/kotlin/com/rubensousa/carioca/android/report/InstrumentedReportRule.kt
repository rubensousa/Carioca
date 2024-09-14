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

import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTestScope
import com.rubensousa.carioca.android.report.stage.internal.InstrumentedTestBuilder
import com.rubensousa.carioca.android.report.suite.SuiteReportRegistry
import com.rubensousa.carioca.android.report.suite.SuiteStage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * The basic report structure.
 *
 * Default implementation is [InstrumentedReportRule].
 *
 * You can extend this class directly to create your own scopes,
 * by returning your own test report class in [createTest]
 */
abstract class AbstractInstrumentedReportRule(
    protected val reporter: CariocaInstrumentedReporter,
    protected val recordingOptions: RecordingOptions,
    protected val screenshotOptions: ScreenshotOptions,
    protected val interceptors: List<CariocaInstrumentedInterceptor>
    = listOf(DumpViewHierarchyInterceptor()),
) : TestWatcher() {

    private var instrumentedTest: InstrumentedTestReport? = null
    private var lastDescription: Description? = null
    private val suiteStage: SuiteStage = SuiteReportRegistry.getSuiteStage()

    abstract fun createTest(description: Description): InstrumentedTestReport

    final override fun starting(description: Description) {
        super.starting(description)
        /**
         * If we're running the same test, we can re-use the previous instance
         * Before doing that, we reset its entire state to ensure we don't keep the old reports
         */
        if (description == lastDescription) {
            instrumentedTest?.reset()
        } else {
            val newTest = createTest(description)
            instrumentedTest = newTest
            suiteStage.addTest(reporter, newTest)
        }
        instrumentedTest?.onStarted()
        lastDescription = description
    }

    final override fun succeeded(description: Description) {
        super.succeeded(description)
        instrumentedTest?.onPassed()
    }

    final override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        instrumentedTest?.onFailed(e)
    }

    protected open fun <T : InstrumentedTestReport> getCurrentTest(): T {
        return requireNotNull(instrumentedTest as T) { "Test not started yet" }
    }

}

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
open class InstrumentedReportRule(
    reporter: CariocaInstrumentedReporter,
    recordingOptions: RecordingOptions = RecordingOptions(),
    screenshotOptions: ScreenshotOptions = ScreenshotOptions(),
    interceptors: List<CariocaInstrumentedInterceptor> = listOf(DumpViewHierarchyInterceptor()),
) : AbstractInstrumentedReportRule(
    reporter = reporter,
    recordingOptions = recordingOptions,
    screenshotOptions = screenshotOptions,
    interceptors = interceptors,
) {

    private val testBuilder = InstrumentedTestBuilder()

    override fun createTest(description: Description): InstrumentedTestReport {
        return testBuilder.build(
            description = description,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            reporter = reporter,
            interceptors = interceptors
        )
    }

    operator fun invoke(block: InstrumentedTestScope.() -> Unit) {
        block(getCurrentTest())
    }

    fun before(block: InstrumentedTestScope.() -> Unit) {
        block(getCurrentTest())
    }

}
