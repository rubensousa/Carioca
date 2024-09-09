package com.rubensousa.carioca

import com.rubensousa.carioca.report.annotations.ScenarioId
import com.rubensousa.carioca.report.stage.TestScenario
import com.rubensousa.carioca.report.stage.ScenarioReportScope

fun sampleScreen(action: SampleScreen.() -> Unit) {
    action(SampleScreen())
}

@ScenarioId("Sample screen Scenario")
class SampleScreenScenario : TestScenario {

    override val name: String = "Sample Scenario"

    override fun report(scope: ScenarioReportScope) = with(scope) {
        step("Step 1 of Scenario") {

        }

        step("Step 2 of Scenario") {

        }
    }

}

class SampleScreen {

    fun assertIsDisplayed() {

    }

    fun assertIsNotDisplayed() {

    }

}
