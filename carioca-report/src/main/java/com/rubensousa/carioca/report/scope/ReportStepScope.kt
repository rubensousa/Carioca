package com.rubensousa.carioca.report.scope

import com.rubensousa.carioca.report.screenshot.CariocaScreenshots

/**
 * Public API for a step block
 */
interface ReportStepScope {

    /**
     * Takes a screenshot with the configuration set in [CariocaScreenshots].
     *
     * The generated file will be pulled from the device once the test runner finishes running all tests
     *
     * @param description the description of the screenshot for the report
     */
    fun screenshot(description: String)


}
