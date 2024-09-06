package com.rubensousa.carioca.core

import org.junit.runner.Description

interface CariocaReporter {

    fun onTestStarted(description: Description)

    fun onStepStarted(step: TestStep)

    fun onStepPassed(step: TestStep)

    fun onStepFailed(step: TestStep)

    fun onTestPassed(description: Description)

    fun onTestFailed(error: Throwable, description: Description)

}
