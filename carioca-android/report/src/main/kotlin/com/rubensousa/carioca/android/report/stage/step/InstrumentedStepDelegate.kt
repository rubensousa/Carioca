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

package com.rubensousa.carioca.android.report.stage.step

import com.rubensousa.carioca.android.report.CariocaInstrumentedReporter
import com.rubensousa.carioca.android.report.interceptor.CariocaInstrumentedInterceptor
import com.rubensousa.carioca.android.report.interceptor.intercept
import com.rubensousa.carioca.android.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.android.report.storage.IdGenerator

internal class InstrumentedStepDelegate(
    private val outputPath: String,
    private val screenshotOptions: ScreenshotOptions,
    private val interceptors: List<CariocaInstrumentedInterceptor>,
    private val reporter: CariocaInstrumentedReporter,
) {

    var currentStep: InstrumentedStepStageImpl? = null
        private set

    fun clearStep() {
        currentStep = null
    }

    fun createStep(title: String, id: String?): InstrumentedStepStageImpl {
        val stepReport = buildStep(title, id)
        currentStep = stepReport
        interceptors.intercept { onStageStarted(stepReport) }
        return stepReport
    }

    fun executeStep(action: InstrumentedStepScope.() -> Unit) {
        val step = requireNotNull(currentStep)
        step.report(action)
        interceptors.intercept { onStagePassed(step) }
        currentStep = null
    }

    private fun buildStep(title: String, id: String?): InstrumentedStepStageImpl {
        val uniqueId = IdGenerator.get()
        val stepId = id ?: uniqueId
        val step = InstrumentedStepStageImpl(
            id = stepId,
            outputPath = outputPath,
            title = title,
            reporter = reporter,
            delegate = this,
            screenshotOptions = screenshotOptions
        )
        return step
    }

}
