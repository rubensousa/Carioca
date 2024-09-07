package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.internal.StepReportDelegate
import com.rubensousa.carioca.report.scope.ReportScenarioScope
import com.rubensousa.carioca.report.scope.ReportStepScope

class ScenarioReport internal constructor(
    id: String,
    val name: String,
    private val delegate: StepReportDelegate,
) : StageReport(id), ReportScenarioScope {

    private val steps = mutableListOf<StepReport>()

    override fun step(title: String, id: String?, action: ReportStepScope.() -> Unit) {
        val step = delegate.step(title, id, action)
        steps.add(step)
    }

    fun getSteps() = steps.toList()

    internal fun report(scenario: TestScenario) {
        scenario.report(this)
        pass()
    }

}
