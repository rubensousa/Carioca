package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.scope.ReportScenarioScope

interface TestScenario {

    fun run(scope: ReportScenarioScope)

}
