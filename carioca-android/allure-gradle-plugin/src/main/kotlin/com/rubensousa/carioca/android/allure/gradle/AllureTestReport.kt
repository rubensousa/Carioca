/*
 * Copyright 2024 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.carioca.android.allure.gradle

import kotlinx.serialization.Serializable

@Serializable
data class AllureTestReport(
    val uuid: String,
    val historyId: String,
    val testCaseId: String,
    val fullName: String,
    val links: List<AllureLink>,
    val labels: List<AllureLabel>,
    val name: String,
    val status: String,
    val description: String? = null,
    val statusDetails: AllureStatusDetail? = null,
    val stage: String,
    val steps: List<AllureStep>,
    val attachments: List<AllureAttachment>,
    val start: Long,
    val stop: Long,
    val parameters: List<Int> = emptyList(),
)

@Serializable
data class AllureContainerReport(
    val uuid: String,
    val name: String,
    val children: List<String>,
    val befores: List<AllureStep> = emptyList(),
    val afters: List<AllureStep> = emptyList(),
    val start: Long,
    val stop: Long,
)

@Serializable
data class AllureStep(
    val name: String,
    val status: String,
    val stage: String,
    val statusDetails: AllureStatusDetail? = null,
    val attachments: List<AllureAttachment>,
    val start: Long,
    val stop: Long,
    val steps: List<AllureStep>,
    val parameters: List<AllureParameter>,
)

@Serializable
data class AllureStatusDetail(
    val known: Boolean,
    val muted: Boolean,
    val flaky: Boolean,
    val message: String,
    val trace: String,
)

@Serializable
data class AllureAttachment(
    val name: String,
    val source: String,
    val type: String,
)

@Serializable
data class AllureParameter(
    val name: String,
    val value: String,
)

@Serializable
data class AllureLink(
    val name: String,
    val url: String,
    val type: String,
)

@Serializable
data class AllureLabel(
    val name: String,
    val value: String,
)
