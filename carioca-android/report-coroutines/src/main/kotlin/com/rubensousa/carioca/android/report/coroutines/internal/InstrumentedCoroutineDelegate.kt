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

package com.rubensousa.carioca.android.report.coroutines.internal

import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineScenario
import com.rubensousa.carioca.android.report.coroutines.InstrumentedCoroutineStageScope
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.android.report.stage.InstrumentedStageReport
import com.rubensousa.carioca.report.runtime.ExecutionIdGenerator
import com.rubensousa.carioca.report.runtime.StageStack

/**
 * Implements the common behavior of all stages, defined by [InstrumentedCoroutineStageScope].
 * All stages should support this basic contract, at a minimum
 */
internal class InstrumentedCoroutineDelegate(
    private val delegateFactory: InstrumentedReportDelegateFactory<InstrumentedCoroutineStageScope>,
    private val screenshotDelegate: ScreenshotDelegate,
    private val host: InstrumentedStageReport,
    private val stack: StageStack<InstrumentedStageReport>,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
    private val outputPath: String,
) : InstrumentedCoroutineStageScope {

    override fun screenshot(description: String, options: ScreenshotOptions?) {
        screenshotDelegate.takeScreenshot(host, description, options)
    }

    override suspend fun step(
        title: String,
        id: String?,
        action: suspend InstrumentedCoroutineStageScope.() -> Unit,
    ) {
        executeStageAwait(createStep(title, id)) { stage ->
            stage.execute(action)
        }
    }

    override suspend fun scenario(scenario: InstrumentedCoroutineScenario) {
        executeStageAwait(createScenario(scenario)) { stage ->
            stage.execute()
        }
    }

    private suspend fun <T : InstrumentedStageReport> executeStageAwait(
        stage: T,
        executor: suspend (T) -> Unit,
    ) {
        onStageStarted(stage)
        executor(stage)
        onStagePassed(stage)
    }

    private fun onStageStarted(stage: InstrumentedStageReport) {
        host.addTestStage(stage)
        stack.push(stage)
        interceptors.intercept { onStageStarted(stage) }
    }

    private fun onStagePassed(stage: InstrumentedStageReport) {
        stack.pop()
        interceptors.intercept { onStagePassed(stage) }
    }

    private fun createStep(title: String, id: String?): InstrumentedCoroutineStep {
        return InstrumentedCoroutineStep(
            outputPath = outputPath,
            delegateFactory = delegateFactory,
            id = id ?: ExecutionIdGenerator.get(),
            title = title
        )
    }

    private fun createScenario(
        scenario: InstrumentedCoroutineScenario,
    ): InstrumentedCoroutineScenarioImpl {
        return InstrumentedCoroutineScenarioImpl(
            outputPath = outputPath,
            delegateFactory = delegateFactory,
            id = scenario.id,
            title = scenario.title,
            scenario = scenario,
        )
    }

}
