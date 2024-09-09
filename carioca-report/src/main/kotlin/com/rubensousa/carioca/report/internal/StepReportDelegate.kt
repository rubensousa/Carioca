package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.CariocaInterceptor
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.scope.ReportStepScope
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

    fun step(title: String, id: String?, action: ReportStepScope.() -> Unit): StepReport {
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
