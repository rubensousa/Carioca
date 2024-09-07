package com.rubensousa.carioca.report.allure

import kotlinx.serialization.Serializable

@Serializable
internal data class CariocaAllureReport(
    val uuid: String,
    val historyId: String,
    val testCaseId: String,
    val fullName: String,
    val links: List<AllureLink>,
    val labels: List<AllureLabel>,
    val name: String,
    val status: String,
    val statusDetails: AllureStatusDetail? = null,
    val stage: String,
    val steps: List<AllureStep>,
    val attachments: List<AllureAttachment>,
    val start: Long,
    val stop: Long,
    val parameters: List<Int> = emptyList()
)

@Serializable
internal data class AllureStep(
    val name: String,
    val status: String,
    val stage: String,
    val statusDetails: AllureStatusDetail? = null,
    val attachments: List<AllureAttachment>,
    val start: Long,
    val stop: Long,
    val steps: List<AllureStep>,
    val parameters: List<Int> = emptyList()
)

@Serializable
internal data class AllureStatusDetail(
    val known: Boolean,
    val muted: Boolean,
    val flaky: Boolean,
    val message: String,
    val trace: String
)

@Serializable
internal data class AllureAttachment(
    val name: String,
    val source: String,
    val type: String,
)

@Serializable
internal data class AllureLink(
    val name: String,
    val url: String,
    val type: String,
)

@Serializable
internal data class AllureLabel(
    val name: String,
    val value: String
)
