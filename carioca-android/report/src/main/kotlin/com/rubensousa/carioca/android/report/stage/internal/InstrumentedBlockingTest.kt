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
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.recording.RecordingOptions
import com.rubensousa.carioca.android.report.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.android.report.stage.InstrumentedScenario
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.android.report.stage.InstrumentedStageScope
import com.rubensousa.carioca.android.report.stage.InstrumentedTestReport
import com.rubensousa.carioca.android.report.stage.InstrumentedTestScope
import com.rubensousa.carioca.junit4.report.TestMetadata

internal class InstrumentedBlockingTest(
    outputPath: String,
    metadata: TestMetadata,
    recordingOptions: RecordingOptions,
    screenshotDelegate: ScreenshotDelegate,
    reporter: CariocaInstrumentedReporter,
    interceptors: List<CariocaInstrumentedInterceptor>,
) : InstrumentedTestReport(
    outputPath = outputPath,
    metadata = metadata,
    recordingOptions = recordingOptions,
    screenshotDelegate = screenshotDelegate,
    reporter = reporter,
    interceptors = interceptors
), InstrumentedTestScope {

    private val delegateFactory =
        object : InstrumentedReportDelegateFactory<InstrumentedStageScope> {
            override fun create(report: InstrumentedStageReport): InstrumentedBlockingDelegate {
                return InstrumentedBlockingDelegate(
                    delegateFactory = this,
                    host = report,
                    screenshotDelegate = screenshotDelegate,
                    stack = stageStack,
                    interceptors = interceptors,
                    outputPath = outputPath,
                )
            }
        }
    private val delegate = delegateFactory.create(this)

    override fun screenshot(description: String, options: ScreenshotOptions?) {
        delegate.screenshot(description, options)
    }

    override fun step(title: String, id: String?, action: InstrumentedStageScope.() -> Unit) {
        delegate.step(title, id, action)
    }

    override fun scenario(scenario: InstrumentedScenario) {
        delegate.scenario(scenario)
    }

    fun before(title: String, action: InstrumentedStageScope.() -> Unit) {
        val stage = InstrumentedBlockingBeforeAfter(
            delegateFactory = delegateFactory,
            title = title,
            outputPath = outputPath,
            before = true
        )
        addStageBefore(stage)
        executeStage(stage) {
            stage.execute(action)
        }
    }

    fun after(title: String, action: InstrumentedStageScope.() -> Unit) {
        val stage = InstrumentedBlockingBeforeAfter(
            delegateFactory = delegateFactory,
            title = title,
            outputPath = outputPath,
            before = false
        )
        addStageAfter(stage)
        executeStage(stage) {
            stage.execute(action)
        }
    }

    private fun executeStage(stage: InstrumentedStageReport, action: () -> Unit) {
        stageStack.push(stage)
        interceptors.intercept { onStageStarted(stage) }
        action()
        stageStack.pop()
        interceptors.intercept { onStagePassed(stage) }
    }

}
