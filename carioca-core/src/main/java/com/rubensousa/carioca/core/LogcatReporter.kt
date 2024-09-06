package com.rubensousa.carioca.core

import android.util.Log
import org.junit.runner.Description

class LogcatReporter : CariocaReporter {

    private val tag = "CariocaReport"

    override fun onTestStarted(description: Description) {
        Log.i(tag, "Test started: $description")
    }

    override fun onTestFailed(error: Throwable, description: Description) {
        Log.e(tag, "Test failed: $description", error)
    }

    override fun onTestPassed(description: Description) {
        Log.i(tag,"Test passed: $description")
    }

    override fun onStepStarted(step: TestStep) {
        Log.i(tag,"Step started: ${step.title}")
    }

    override fun onStepPassed(step: TestStep) {
        Log.i(tag,"Step passed: ${step.title}")
    }

    override fun onStepFailed(step: TestStep) {
        Log.i(tag,"Step failed: ${step.title}")
    }

}
