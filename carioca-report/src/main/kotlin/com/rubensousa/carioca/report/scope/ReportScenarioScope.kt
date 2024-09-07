package com.rubensousa.carioca.report.scope

interface ReportScenarioScope {
    /**
     * Creates an individual section of a scenario
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: ReportStepScope.() -> Unit)
}
