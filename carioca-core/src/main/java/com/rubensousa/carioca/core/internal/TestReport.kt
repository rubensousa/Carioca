package com.rubensousa.carioca.core.internal

internal data class TestReport(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val tests: List<Test>,
)
