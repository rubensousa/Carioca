package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.internal.StepReportDelegate

interface ScenarioReportScope {
    /**
     * Creates an individual section of a scenario
     *
     * @param title the name of the step
     * @param id an optional persistent step id
     * @param action the step block that will be executed
     */
    fun step(title: String, id: String? = null, action: StepReportScope.() -> Unit)
}

class ScenarioReport internal constructor(
    id: String,
    val name: String,
    private val delegate: StepReportDelegate,
) : StageReport(id), ScenarioReportScope {

    private val steps = mutableListOf<StepReport>()

    override fun step(title: String, id: String?, action: StepReportScope.() -> Unit) {
        val step = delegate.step(title, id, action)
        steps.add(step)
    }

    fun getSteps() = steps.toList()

    internal fun report(scenario: TestScenario) {
        scenario.report(this)
        pass()
    }

}
