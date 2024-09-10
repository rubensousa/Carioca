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

package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.ReportStatus
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.BufferedOutputStream
import java.io.OutputStream

/**
 * Represents the test report in a json format
 */
class CariocaSimpleJsonReporter : CariocaReporter {

    private val startTimeKey = "startTime"
    private val endTimeKey = "endTime"
    private val executionIdKey = "executionId"
    private val titleKey = "title"
    private val statusKey = "status"

    override fun getOutputDir(report: TestReport): String {
        return "${report.className}/${report.methodName}"
    }

    override fun getReportFilename(report: TestReport): String {
        return "${report.executionId}_report.json"
    }

    override fun getScreenshotName(id: String): String {
        return id
    }

    override fun getRecordingName(id: String): String {
        return id
    }

    override fun writeTestReport(report: TestReport, outputStream: OutputStream) {
        val metadata = getTestResult(report)
        serialize(metadata.toJson(), outputStream)
    }

    private fun serialize(json: String, outputStream: OutputStream) {
        BufferedOutputStream(outputStream).use { stream ->
            stream.write(json.toByteArray())
            stream.flush()
        }
    }

    private fun getTestResult(test: TestReport): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val stages = mutableListOf<Map<String, Any?>>()
        test.getStageReports().forEach { stage ->
            if (stage is ScenarioReport) {
                stages.add(getScenarioResult(stage))
            } else if (stage is StepReport) {
                stages.add(getStepResult(stage))
            }
        }
        map[executionIdKey] = test.executionId
        map["testId"] = test.id
        map[startTimeKey] = test.startTime
        map[endTimeKey] = test.endTime
        map["testClass"] = test.className
        map["testName"] = test.methodName
        map["testDescription"] = test.title
        map.putStatus(test.status)
        map["stages"] = stages
        test.getFailureCause()?.let { cause ->
            map["failure"] = mapOf(
                "message" to cause.message,
                "stacktrace" to cause.stackTraceToString()
            )
        }
        return map
    }

    private fun getScenarioResult(scenario: ScenarioReport): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val steps = mutableListOf<Map<String, Any>>()
        scenario.getSteps().forEach { step ->
            steps.add(getStepResult(step))
        }
        map[executionIdKey] = scenario.executionId
        map[startTimeKey] = scenario.startTime
        map[endTimeKey] = scenario.endTime
        map["scenarioId"] = scenario.id
        map["steps"] = steps
        return mapOf(
            "type" to "scenario",
            "properties" to map
        )
    }

    private fun getStepResult(step: StepReport): Map<String, Any> {
        val map = mutableMapOf<String, Any?>()
        val steps = mutableListOf<Map<String, Any?>>()
        step.getSteps().forEach { nestedStep ->
            steps.add(getStepResult(nestedStep))
        }
        val screenshots = mutableListOf<Map<String, Any>>()
        step.getScreenshots().forEach { screenshot ->
            screenshots.add(
                mapOf(
                    "file" to screenshot.path,
                    "description" to screenshot.description
                )
            )
        }
        map[executionIdKey] = step.executionId
        map[titleKey] = step.title
        map[startTimeKey] = step.startTime
        map[endTimeKey] = step.endTime
        map.putStatus(step.status)
        if (screenshots.isNotEmpty()) {
            map["screenshots"] = screenshots
        } else {
            map["screenshots"] = null
        }
        if (steps.isNotEmpty()) {
            map["steps"] = steps
        }
        return mapOf(
            "type" to "step",
            "properties" to map,
        )
    }

    private fun MutableMap<String, Any?>.putStatus(status: ReportStatus) {
        this[statusKey] = status.name.lowercase()
    }

    private fun Map<String, Any?>.toJson() = toJsonElement().toString()

    private fun Map<*, *>.toJsonElement(): JsonElement {
        return JsonObject(
            mapNotNull {
                (it.key as? String ?: return@mapNotNull null) to it.value.toJsonElement()
            }.toMap(),
        )
    }

    private fun Collection<*>.toJsonElement(): JsonElement {
        return JsonArray(mapNotNull { it.toJsonElement() })
    }

    private fun Any?.toJsonElement(): JsonElement {
        return when (this) {
            null -> JsonNull
            is Map<*, *> -> toJsonElement()
            is Collection<*> -> toJsonElement()
            is Long -> JsonPrimitive(this)
            is Int -> JsonPrimitive(this)
            else -> JsonPrimitive(toString())
        }
    }

}
