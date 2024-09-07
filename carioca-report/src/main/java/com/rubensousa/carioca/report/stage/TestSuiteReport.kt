package com.rubensousa.carioca.report.stage

data class TestSuiteReport(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val status: ReportStatus,
    val tests: List<TestReport>,
)
