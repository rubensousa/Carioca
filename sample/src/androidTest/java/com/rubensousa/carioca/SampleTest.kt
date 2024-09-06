package com.rubensousa.carioca

import com.rubensousa.carioca.core.CariocaReportRule
import com.rubensousa.carioca.core.LogcatReporter
import com.rubensousa.carioca.core.report
import org.junit.Rule
import org.junit.Test


class SampleTest {

    @get:Rule
    val cariocaReportRule = CariocaReportRule(reporter = LogcatReporter())

    @Test
    fun testSomethingHappens() = report(cariocaReportRule) {
        step("Open Screen 1") {
            screenshot("Save this")
            screenshot("Save that")
        }

        step("Open Screen 2") {
            screenshot("Save this")
        }

    }

}
