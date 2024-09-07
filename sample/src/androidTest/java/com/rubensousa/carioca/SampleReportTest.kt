package com.rubensousa.carioca

import androidx.test.espresso.Espresso
import com.rubensousa.carioca.report.CariocaReportRule
import com.rubensousa.carioca.report.LogcatReport
import com.rubensousa.carioca.report.annotations.TestId
import org.junit.Rule
import org.junit.Test

class SampleReportTest {

    @get:Rule
    val reportRule = CariocaReportRule(reporter = LogcatReport())

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
