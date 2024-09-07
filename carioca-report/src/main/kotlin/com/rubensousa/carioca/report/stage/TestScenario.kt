package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.scope.ReportScenarioScope

interface TestScenario {

    val name: String

    fun report(scope: ReportScenarioScope)

}
