package com.rubensousa.carioca.report.internal

import android.net.Uri
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.scope.ReportStepScope
import com.rubensousa.carioca.report.stage.StepReport

internal class StepReportDelegate(
    private val outputDir: Uri,
    private val reports : List<CariocaReporter>,
) {

    var currentStep: StepReport? = null
        private set

    fun clearStep() {
        currentStep = null
    }

    fun step(title: String, id: String?, action: ReportStepScope.() -> Unit): StepReport {
        val stepReport = createStepReport(title, id)
        currentStep = stepReport
        forEachReport { onStepStarted(stepReport) }
        stepReport.report(action)
        forEachReport { onStepPassed(stepReport) }
        currentStep = null
        return stepReport
    }

    private fun createStepReport(title: String, id: String?): StepReport {
        val uniqueId = IdGenerator.get()
        val stepId = id ?: uniqueId
        val step = StepReport(
            id = stepId,
            testOutputDir = outputDir,
            title = title,
        )
        return step
    }


    private fun forEachReport(action: CariocaReporter.() -> Unit) {
        reports.forEach { report ->
            action(report)
        }
    }

}
