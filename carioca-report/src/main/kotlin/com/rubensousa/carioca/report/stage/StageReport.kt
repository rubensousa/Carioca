package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.internal.IdGenerator

abstract class StageReport(val id: String) {

    var status = ReportStatus.SKIPPED
        private set

    var startTime = System.currentTimeMillis()
        private set

    var endTime = startTime
        private set

    val executionId = IdGenerator.get()

    internal fun pass() {
        status = ReportStatus.PASSED
        saveEndTime()
    }

    internal fun fail() {
        status = ReportStatus.FAILED
        saveEndTime()
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

}
