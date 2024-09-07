package com.rubensousa.carioca.report

import android.net.Uri
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

    override fun getOutputDir(report: TestReport, outputDir: Uri): String {
        return "${outputDir.path}/${report.className}/${report.name}"
    }

    override fun getReportFilename(report: TestReport): String {
        return "${report.executionId}_report.json"
    }

    override fun getScreenshotName(id: String): String {
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
        map["testName"] = test.name
        map.putStatus(test.status)
        map["stages"] = stages
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
