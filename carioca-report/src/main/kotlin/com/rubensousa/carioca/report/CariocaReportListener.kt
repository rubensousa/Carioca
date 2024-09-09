package com.rubensousa.carioca.report

import android.annotation.SuppressLint
import androidx.test.internal.runner.listener.InstrumentationRunListener
import com.rubensousa.carioca.report.internal.TestReportBuilder
import org.junit.runner.Description
import org.junit.runner.Result

@Suppress("unused")
@SuppressLint("RestrictedApi")
class CariocaReportListener : InstrumentationRunListener() {

    private val reportBuilder = TestReportBuilder

    override fun testRunStarted(description: Description) {
        super.testRunStarted(description)
        reportBuilder.reset()
    }

    override fun testRunFinished(result: Result) {
        super.testRunFinished(result)
        // TODO: compile final suite report
        // val report = reportBuilder.buildSuiteReport()
        // TestReportWriter.write(report)
    }

}
