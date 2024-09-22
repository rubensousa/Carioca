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

import com.rubensousa.carioca.junit4.report.getTestMetadata
import com.rubensousa.carioca.junit4.report.getTestReportConfig
import com.rubensousa.carioca.report.android.recording.RecordingOptions
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.stage.InstrumentedTestReport
import com.rubensousa.carioca.report.android.suite.SuiteReportRegistry
import com.rubensousa.carioca.report.runtime.TestMetadata
import com.rubensousa.carioca.report.runtime.TestReportConfig
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
    protected val reporter: InstrumentedReporter,
    protected val recordingOptions: RecordingOptions,
    protected val screenshotOptions: ScreenshotOptions,
) : TestWatcher() {

    private var instrumentedTest: InstrumentedTestReport? = null
    private var lastDescription: Description? = null

    protected abstract fun createTest(
        reportConfig: TestReportConfig?,
        testMetadata: TestMetadata,
        recordingOptions: RecordingOptions,
        screenshotOptions: ScreenshotOptions,
    ): InstrumentedTestReport

    final override fun starting(description: Description) {
        /**
         * If we're running the same test, we can re-use the previous instance
         * Before doing that, we reset its entire state to ensure we don't keep the old reports
         */
        if (description == lastDescription) {
            instrumentedTest?.reset()
        } else {
            val newTest = createTest(
                reportConfig = description.getTestReportConfig(),
                testMetadata = description.getTestMetadata(),
                recordingOptions = RecordingOptions.from(description) ?: recordingOptions,
                screenshotOptions = ScreenshotOptions.from(description) ?: screenshotOptions
            )
            instrumentedTest = newTest
            SuiteReportRegistry.getSuiteStage().apply {
                registerReporter(reporter)
                testStarted(newTest)
            }
        }
        instrumentedTest?.onStarted()
        lastDescription = description
    }

    final override fun succeeded(description: Description) {
        instrumentedTest?.onPassed()
    }

    final override fun failed(e: Throwable, description: Description) {
        instrumentedTest?.onFailed(e)
    }

    //  Internal for testing
    internal fun start(description: Description) {
        starting(description)
    }

    // Internal for testing
    internal fun pass(description: Description) {
        succeeded(description)
    }

    // Internal for testing
    internal fun fail(error: Throwable, description: Description) {
        failed(error, description)
    }

    // Visible for testing
    internal fun getCurrentReport() = getCurrentTest<InstrumentedTestReport>()

    protected open fun <T : InstrumentedTestReport> getCurrentTest(): T {
        return requireNotNull(instrumentedTest as T) { "Test not started yet" }
    }

}
