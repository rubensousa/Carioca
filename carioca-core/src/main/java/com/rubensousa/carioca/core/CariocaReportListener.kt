package com.rubensousa.carioca.core

import android.annotation.SuppressLint
import androidx.test.internal.runner.listener.InstrumentationRunListener
import com.rubensousa.carioca.core.internal.TestReportBuilder
import com.rubensousa.carioca.core.internal.TestReportWriter
import org.junit.runner.Description
import org.junit.runner.Result

@SuppressLint("RestrictedApi")
class CariocaReportListener : InstrumentationRunListener() {

    private val reportBuilder = TestReportBuilder

    override fun testRunStarted(description: Description) {
        super.testRunStarted(description)
        reportBuilder.reset()
    }

    override fun testRunFinished(result: Result) {
        super.testRunFinished(result)
        val report = reportBuilder.build()
        TestReportWriter.write(report)
    }

}
