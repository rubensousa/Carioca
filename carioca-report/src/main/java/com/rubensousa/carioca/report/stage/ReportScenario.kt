package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.scope.ReportScenarioScope

class ReportScenario(
    id: String,
    private val delegate: TestScenario,
) : ReportStage(id) {

    internal fun run(scope: ReportScenarioScope) {
        delegate.run(scope)
        pass()
    }

}
