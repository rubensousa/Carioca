package com.rubensousa.carioca.report.stage

interface TestScenario {

    val name: String

    fun report(scope: ScenarioReportScope)

}
