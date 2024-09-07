package com.rubensousa.carioca.report.scope

import com.rubensousa.carioca.report.stage.TestScenario

/**
 * The public API for each report. This is the main entry for each test report.
 */
interface ReportTestScope {

    /**
     * Creates an individual section of a test
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: ReportStepScope.() -> Unit)

    /**
     * Creates a report for a set of steps.
     * This is almost equivalent to calling [step] multiple times, but in a more re-usable way
     */
    fun scenario(scenario: TestScenario)

}
