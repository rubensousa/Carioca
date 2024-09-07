package com.rubensousa.carioca.core.internal

import org.junit.runner.Description

internal object TestReportBuilder {

    private val tests = mutableListOf<Test>()
    private var startTime = System.currentTimeMillis()

    fun newTest(description: Description): Test {
        val test = createTest(description)
        tests.add(test)
        return test
    }

    fun reset() {
        startTime = System.currentTimeMillis()
        tests.clear()
    }

    fun build(): TestReport {
        return TestReport(
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            tests = tests.toList(),
            id = IdGenerator.get()
        )
    }

    private fun createTest(description: Description): Test {
        return Test(
            id = IdGenerator.get(),
            name = description.methodName,
            className = description.className,
            outputDir = TestOutputLocation.getOutputPath(description)
        )
    }

}
