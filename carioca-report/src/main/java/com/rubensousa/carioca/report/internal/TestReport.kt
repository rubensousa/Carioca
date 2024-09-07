package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.stage.ReportTest

internal data class TestReport(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val tests: List<ReportTest>,
)
