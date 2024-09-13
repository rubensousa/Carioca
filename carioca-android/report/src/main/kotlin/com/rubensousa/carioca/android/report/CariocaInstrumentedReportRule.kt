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

import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineTestScope
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.DumpViewHierarchyInterceptor
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTest
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTestBuilder
import com.rubensousa.carioca.android.report.stage.test.InstrumentedTestScope
import com.rubensousa.carioca.android.report.suite.SuiteReportRegistry
import com.rubensousa.carioca.android.report.suite.SuiteStage
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A test rule that builds a detailed report for a test, including its steps.
 *
 * Start by calling [report] to start the report. Then use either [InstrumentedTestScope.step] or [InstrumentedTestScope.scenario]
 * to start describing the report in detail.
 *
 * You can also extend this class to provide a default report configuration across all tests:
 *
 * ```kotlin
 * class SampleReportRule : CariocaReportRule(
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
 */
open class CariocaInstrumentedReportRule(
    private val reporter: CariocaInstrumentedReporter,
    private val recordingOptions: RecordingOptions = RecordingOptions(),
    private val screenshotOptions: ScreenshotOptions = ScreenshotOptions(),
    private val interceptors: List<CariocaInstrumentedInterceptor> = listOf(DumpViewHierarchyInterceptor()),
) : TestWatcher() {

    private val builder = InstrumentedTestBuilder()
    private var instrumentedTest: InstrumentedTest? = null
    private val suiteStage: SuiteStage = SuiteReportRegistry.getSuiteStage()

    operator fun invoke(block: InstrumentedTestScope.() -> Unit) {
        block(getCurrentTest())
    }

    /**
     * Runs the report inside a coroutine scope
     */
    fun runTest(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend InstrumentedCoroutineTestScope.() -> Unit,
    ) {
        val currentTest = getCurrentTest()
        kotlinx.coroutines.test.runTest(context) {
            block(currentTest)
        }
    }

    final override fun starting(description: Description) {
        super.starting(description)
        // TODO: Get the previous instance if this test was retried with a retry rule
        val test = builder.build(
            description = description,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            interceptors = interceptors,
            reporter = reporter
        )
        instrumentedTest = test
        suiteStage.addTest(reporter, test)
        test.starting()
    }

    final override fun succeeded(description: Description) {
        super.succeeded(description)
        getCurrentTest().succeeded()
        instrumentedTest = null
    }

    final override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        getCurrentTest().failed(e)
        instrumentedTest = null
    }

    private fun getCurrentTest(): InstrumentedTest {
        return requireNotNull(instrumentedTest) { "Test not started yet" }
    }

}
