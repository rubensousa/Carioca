package com.rubensousa.carioca.report

import android.annotation.SuppressLint
import android.os.Build
import androidx.test.internal.runner.listener.InstrumentationRunListener
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.internal.TestReportBuilder
import com.rubensousa.carioca.report.internal.TestReportWriter
import org.junit.runner.Description
import org.junit.runner.Result

@SuppressLint("RestrictedApi")
class CariocaReportListener : InstrumentationRunListener() {

    private val reportBuilder = TestReportBuilder

    override fun testRunStarted(description: Description) {
        super.testRunStarted(description)
        /**
         * Ensures the target app can read the screen recordings collected
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .executeShellCommand("appops set --uid $packageName READ_MEDIA_VIDEO allow")
        }
        reportBuilder.reset()
    }

    override fun testRunFinished(result: Result) {
        super.testRunFinished(result)
        // TODO
        // val report = reportBuilder.buildSuiteReport()
        // TestReportWriter.write(report)
    }

}
