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

import com.rubensousa.carioca.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.test.InstrumentedTestScope
import com.rubensousa.carioca.report.stage.test.InstrumentedTestStageImpl
import com.rubensousa.carioca.report.suite.SuiteReportRegistry
import com.rubensousa.carioca.report.suite.SuiteStage
import org.junit.rules.TestWatcher
import org.junit.runner.Description


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
    private val interceptors: List<CariocaInstrumentedInterceptor> = emptyList(),
) : TestWatcher() {

    private var testStage: InstrumentedTestStageImpl? = null
    private val suiteStage: SuiteStage = SuiteReportRegistry.getSuiteStage()

    operator fun invoke(block: InstrumentedTestScope.() -> Unit) {
        block(getCurrentReport())
    }

    final override fun starting(description: Description) {
        super.starting(description)
        val newStage = createTestStage(
            description = description,
            recordingOptions = recordingOptions,
            screenshotOptions = screenshotOptions,
            interceptors = interceptors,
            reporter = reporter
        )
        testStage = newStage
        suiteStage.addTest(reporter, newStage)
        getCurrentReport().starting(description)
    }

    final override fun succeeded(description: Description) {
        super.succeeded(description)
        getCurrentReport().succeeded()
        testStage = null
    }

    final override fun failed(e: Throwable, description: Description) {
        super.failed(e, description)
        getCurrentReport().failed(e)
        testStage = null
    }

    // TODO: Get the previous instance if this test was retried with a retry rule
    private fun createTestStage(
        description: Description,
        recordingOptions: RecordingOptions,
        screenshotOptions: ScreenshotOptions,
        interceptors: List<CariocaInstrumentedInterceptor>,
        reporter: CariocaInstrumentedReporter,
    ): InstrumentedTestStageImpl {
        return InstrumentedTestStageImpl(
            id = getTestId(description),
            title = getTestTitle(description),
            recordingOptions = recordingOptions,
            methodName = description.methodName,
            className = description.className,
            packageName = description.testClass.`package`?.name ?: "",
            interceptors = interceptors,
            screenshotOptions = screenshotOptions,
            reporter = reporter
        )
    }

    private fun getTestId(description: Description): String {
        val testId = description.getAnnotation(TestId::class.java)
        return testId?.id ?: getDefaultTestId(description)
    }

    private fun getTestTitle(description: Description): String {
        val annotation = description.getAnnotation(TestTitle::class.java)
        return annotation?.title ?: description.methodName
    }

    private fun getDefaultTestId(description: Description): String {
        return "${description.className}.${description.methodName}"
    }

    private fun getCurrentReport(): InstrumentedTestStageImpl {
        return requireNotNull(testStage) { "Test not started yet" }
    }

}
