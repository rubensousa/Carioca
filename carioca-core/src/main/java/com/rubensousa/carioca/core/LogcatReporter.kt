package com.rubensousa.carioca.core

import android.util.Log
import org.junit.runner.Description

class LogcatReporter : CariocaReporter {

    private val tag = "CariocaLogcatReport"

    override fun onTestStarted(description: Description) {
        Log.i(tag, "Test started: $description")
    }

    override fun onTestFailed(error: Throwable, description: Description) {
        Log.e(tag, "Test failed: $description", error)
    }

    override fun onTestPassed(description: Description) {
        Log.i(tag,"Test passed: $description")
    }

}
