package com.rubensousa.carioca

import com.rubensousa.carioca.report.annotations.ScenarioId
import com.rubensousa.carioca.report.stage.TestScenario
import com.rubensousa.carioca.report.scope.ReportScenarioScope

fun sampleScreen(action: SampleScreen.() -> Unit) {
    action(SampleScreen())
}

@ScenarioId("Sample screen Scenario")
class SampleScreenScenario : TestScenario {

    override fun run(scope: ReportScenarioScope) = with(scope) {
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
