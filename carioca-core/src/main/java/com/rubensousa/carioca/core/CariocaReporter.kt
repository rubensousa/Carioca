package com.rubensousa.carioca.core

import org.junit.runner.Description

interface CariocaReporter {

    fun onTestStarted(description: Description)

    fun onTestFailed(error: Throwable, description: Description)

    fun onTestPassed(description: Description)

}
