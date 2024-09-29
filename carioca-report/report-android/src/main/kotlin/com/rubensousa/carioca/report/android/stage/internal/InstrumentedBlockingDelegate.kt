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

package com.rubensousa.carioca.report.android.stage.internal

import com.rubensousa.carioca.report.android.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.report.android.interceptor.intercept
import com.rubensousa.carioca.report.android.screenshot.ScreenshotDelegate
import com.rubensousa.carioca.report.android.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.android.stage.InstrumentedReportDelegateFactory
import com.rubensousa.carioca.report.android.stage.InstrumentedScenario
import com.rubensousa.carioca.report.android.stage.InstrumentedStageReport
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope
import com.rubensousa.carioca.report.android.stage.InstrumentedStageType
import com.rubensousa.carioca.report.android.storage.ReportStorageProvider
import com.rubensousa.carioca.report.runtime.ExecutionIdGenerator
import com.rubensousa.carioca.report.runtime.StageStack

/**
 * Implements the common behavior of all stages, defined by [InstrumentedStageScope].
 * All stages support this basic contract
 */
internal class InstrumentedBlockingDelegate(
    private val host: InstrumentedStageReport,
    private val screenshotDelegate: ScreenshotDelegate,
    private val delegateFactory: InstrumentedReportDelegateFactory<InstrumentedStageScope>,
    private val stack: StageStack<InstrumentedStageReport>,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
    private val outputPath: String,
    private val storageProvider: ReportStorageProvider,
) : InstrumentedStageScope {

    override fun screenshot(description: String, options: ScreenshotOptions?) {
        screenshotDelegate.takeScreenshot(host, description, options)
    }

    override fun step(
        title: String,
        id: String?,
        action: InstrumentedStageScope.() -> Unit,
    ) {
        execute(createStep(title, id)) { stage ->
            stage.execute(action)
        }
    }

    override fun scenario(scenario: InstrumentedScenario) {
        execute(createScenario(scenario)) { stage ->
            stage.executeScenario(scenario)
        }
    }

    override fun param(key: String, value: String) {
        host.setParameter(key, value)
    }

    private fun <T : InstrumentedStageReport> execute(
        stage: T,
        executor: (T) -> Unit,
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

    private fun createStep(title: String, id: String?): InstrumentedBlockingStage {
        return InstrumentedBlockingStage(
            id = id ?: ExecutionIdGenerator.get(),
            title = title,
            type = InstrumentedStageType.STEP,
            outputPath = outputPath,
            delegateFactory = delegateFactory,
            storageProvider = storageProvider
        )
    }

    private fun createScenario(
        scenario: InstrumentedScenario,
    ): InstrumentedBlockingStage {
        return InstrumentedBlockingStage(
            id = scenario.id,
            title = scenario.title,
            type = InstrumentedStageType.SCENARIO,
            outputPath = outputPath,
            delegateFactory = delegateFactory,
            storageProvider = storageProvider
        )
    }

}
