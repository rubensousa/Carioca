package com.rubensousa.carioca

import com.rubensousa.carioca.report.annotations.TestId
import org.junit.Rule
import org.junit.Test

class SampleTestReport {

    @get:Rule
    val reportRule = AllureReportRule()

    @TestId(id = "This is a persistent test id")
    @Test
    fun testSomethingHappens() = reportRule.report {
        scenario(SampleScreenScenario())

        step("Setup some state") {
            screenshot("Save this")
        }
        step("Open Screen 2") {
            sampleScreen {
                assertIsDisplayed()
                screenshot("Sample screen displayed")
            }

            sampleScreen {
                assertIsNotDisplayed()
                screenshot("Launcher")
            }
        }
    }

}
