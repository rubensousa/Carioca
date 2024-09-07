package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.CariocaLogger
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.stage.StepReport

internal class StepReportDelegate(
    private val outputPath: String,
    private val logger: CariocaLogger?,
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
        logger?.onStepStarted(stepReport)
        stepReport.report(action)
        logger?.onStepPassed(stepReport)
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
            reporter = reporter
        )
        return step
    }

}
