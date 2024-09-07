package com.rubensousa.carioca.core.internal

import android.net.Uri
import com.rubensousa.carioca.core.ReportStatus
import com.rubensousa.carioca.core.TestStep

internal class Test(
    val id: String,
    val name: String,
    val className: String,
    val outputDir: Uri,
) {

    var startTime = System.currentTimeMillis()
        private set

    var endTime = startTime
        private set

    var status = ReportStatus.SKIPPED
        private set

    private val steps = mutableListOf<TestStep>()

    fun newStep(title: String): TestStep {
        val stepId = IdGenerator.get()
        val step = TestStep(
            id = stepId,
            outputDir = TestOutputLocation.getStepUri(outputDir, stepId),
            title = title
        )
        steps.add(step)
        return step
    }

    fun pass() {
        endTime = System.currentTimeMillis()
        status = ReportStatus.PASSED
    }

    fun fail() {
        endTime = System.currentTimeMillis()
        status = ReportStatus.FAILED
    }

    fun getSteps(): List<TestStep> = steps.toList()

    fun getLastStep(): TestStep? = steps.lastOrNull()

    override fun toString(): String {
        return "Test(id='$id', name='$name', className='$className')"
    }

}
