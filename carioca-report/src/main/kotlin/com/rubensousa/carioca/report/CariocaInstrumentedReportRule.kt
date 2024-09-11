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

package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.interceptor.CariocaInterceptor
import com.rubensousa.carioca.report.internal.TestReportBuilder
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.TestReportImpl
import com.rubensousa.carioca.report.stage.TestReportScope
import org.junit.rules.TestWatcher
import org.junit.runner.Description


/**
 * A test rule that builds a detailed report for a test, including its steps.
 *
 * Start by calling [report] to start the report. Then use either [TestReportScope.step] or [TestReportScope.scenario]
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
 * val reportRule = SampleReportRule()
 *
 * @Test
 * fun sampleTest() = reportRule.report {
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
    private val interceptors: List<CariocaInterceptor> = emptyList(),
) : TestWatcher() {

    private var test: TestReportImpl? = null
    private val reportBuilder = TestReportBuilder

    final override fun starting(description: Description) {
        super.starting(description)
        test = reportBuilder.newTest(
            description = description,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            interceptors = interceptors,
            reporter = reporter
        )
        getCurrentTest().starting(description)
    }

    final override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        getCurrentTest().failed(e, description)
        test = null
    }

    final override fun succeeded(description: Description) {
        super.succeeded(description)
        getCurrentTest().succeeded(description)
        test = null
    }

    fun report(block: TestReportScope.() -> Unit) {
        block(getCurrentTest())
    }

    private fun getCurrentTest(): TestReportImpl {
        return requireNotNull(test) { "Test not started yet" }
    }

}
