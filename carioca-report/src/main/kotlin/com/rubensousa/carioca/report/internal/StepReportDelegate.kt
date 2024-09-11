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
import com.rubensousa.carioca.report.interceptor.CariocaInterceptor
import com.rubensousa.carioca.report.interceptor.intercept
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.StepReportImpl
import com.rubensousa.carioca.report.stage.StepReportScope
import com.rubensousa.carioca.report.stage.TestReportImpl

internal class StepReportDelegate(
    private val report: TestReportImpl,
    private val outputPath: String,
    private val screenshotOptions: ScreenshotOptions,
    private val interceptors: List<CariocaInterceptor>,
    private val reporter: CariocaInstrumentedReporter,
) {

    var currentStep: StepReportImpl? = null
        private set

    fun clearStep() {
        currentStep = null
    }

    fun createStep(title: String, id: String?): StepReportImpl {
        val stepReport = createStepReport(title, id)
        currentStep = stepReport
        interceptors.intercept { onStepStarted(report, stepReport) }
        return stepReport
    }

    fun executeStep(action: StepReportScope.() -> Unit) {
        val step = requireNotNull(currentStep)
        step.report(action)
        interceptors.intercept { onStepPassed(report, step) }
        currentStep = null
    }

    private fun createStepReport(title: String, id: String?): StepReportImpl {
        val uniqueId = IdGenerator.get()
        val stepId = id ?: uniqueId
        val step = StepReportImpl(
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
