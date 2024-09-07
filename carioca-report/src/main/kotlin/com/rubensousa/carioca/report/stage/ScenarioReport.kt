package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.scope.ReportScenarioScope
import com.rubensousa.carioca.report.scope.ReportStepScope

class ScenarioReport internal constructor(
    id: String,
    private val delegate: StepReportDelegate,
) : StageReport(id), ReportScenarioScope {

    private val steps = mutableListOf<StepReport>()

    override fun step(title: String, id: String?, action: ReportStepScope.() -> Unit) {
        val step = delegate.step(title, id, action)
        steps.add(step)
    }

    internal fun report(scenario: TestScenario) {
        scenario.report(this)
        pass()
    }

    internal fun getSteps() = steps.toList()

}
