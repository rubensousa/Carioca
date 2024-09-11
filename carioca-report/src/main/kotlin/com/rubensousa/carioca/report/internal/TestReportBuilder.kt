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

import com.rubensousa.carioca.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.report.TestId
import com.rubensousa.carioca.report.TestTitle
import com.rubensousa.carioca.report.interceptor.CariocaInterceptor
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.ExecutionStatus
import com.rubensousa.carioca.report.stage.TestReport
import com.rubensousa.carioca.report.stage.TestReportImpl
import com.rubensousa.carioca.report.stage.TestSuiteReport
import org.junit.runner.Description

internal object TestReportBuilder {

    private val tests = mutableListOf<TestReport>()
    private var startTime = System.currentTimeMillis()

    // TODO: Get the previous test if this test was retried with a retry rule
    fun newTest(
        description: Description,
        recordingOptions: RecordingOptions,
        screenshotOptions: ScreenshotOptions,
        interceptors: List<CariocaInterceptor>,
        reporter: CariocaInstrumentedReporter,
    ): TestReportImpl {
        val test = TestReportImpl(
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
        tests.add(test)
        return test
    }

    fun reset() {
        startTime = System.currentTimeMillis()
        tests.clear()
    }

    // TODO: Write the suite report too, since we want to know the suite duration
    fun buildSuiteReport(): TestSuiteReport {
        val hasAnyFailure = tests.any { it.getMetadata().execution.status == ExecutionStatus.FAILED }
        return TestSuiteReport(
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            tests = tests.toList(),
            status = if (hasAnyFailure) {
                ExecutionStatus.FAILED
            } else {
                ExecutionStatus.PASSED
            },
            id = IdGenerator.get()
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

}
