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

import com.rubensousa.carioca.report.CariocaInterceptor
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.stage.StepReportScope
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions
import com.rubensousa.carioca.report.stage.StepReport

internal class StepReportDelegate(
    private val outputPath: String,
    private val screenshotOptions: ScreenshotOptions,
    private val interceptor: CariocaInterceptor?,
    private val reporter: CariocaReporter,
) {

    var currentStep: StepReport? = null
        private set

    fun clearStep() {
        currentStep = null
    }

    fun step(title: String, id: String?, action: StepReportScope.() -> Unit): StepReport {
        val stepReport = createStepReport(title, id)
        currentStep = stepReport
        interceptor?.onStepStarted(stepReport)
        stepReport.report(action)
        interceptor?.onStepPassed(stepReport)
        currentStep = null
        return stepReport
    }

    private fun createStepReport(title: String, id: String?): StepReport {
        val uniqueId = IdGenerator.get()
        val stepId = id ?: uniqueId
        val step = StepReport(
            id = stepId,
            outputPath = outputPath,
            title = title,
            reporter = reporter,
            screenshotOptions = screenshotOptions
        )
        return step
    }

}
