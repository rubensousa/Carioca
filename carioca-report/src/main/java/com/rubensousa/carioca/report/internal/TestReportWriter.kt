package com.rubensousa.carioca.report.internal

import com.rubensousa.carioca.report.stage.ReportScenario
import com.rubensousa.carioca.report.stage.ReportStatus
import com.rubensousa.carioca.report.stage.ReportTest
import com.rubensousa.carioca.report.stage.ReportStep
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.BufferedOutputStream


internal object TestReportWriter {

    private val outputFilename = "carioca_report.json"
    private val startTimeKey = "startTime"
    private val endTimeKey = "endTime"
    private val idKey = "id"
    private val titleKey = "title"
    private val statusKey = "status"

    fun write(report: TestReport) {
        val output = mutableMapOf<String, Any>()
        val testResults = mutableListOf<Map<String, Any>>()
        var status = ReportStatus.PASSED

        report.tests.forEach { test ->
            if (test.status == ReportStatus.FAILED) {
                status = ReportStatus.FAILED
            }
            testResults.add(getTestResult(test))
        }

        output[idKey] = report.id
        output[startTimeKey] = report.startTime
        output[endTimeKey] = report.endTime
        output.putStatus(status)
        output["tests"] = testResults

        val outputStream = TestOutputLocation.getGlobalReportOutputStream(outputFilename)
        BufferedOutputStream(outputStream).use { stream ->
            val json = output.toJson()
            stream.write(json.toByteArray())
            stream.flush()
        }
    }

    private fun getTestResult(test: ReportTest): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val stages = mutableListOf<Map<String, Any>>()
        test.getStages().forEach { stage ->
            if (stage is ReportScenario) {
                stages.add(emptyMap())
            } else if (stage is ReportStep) {
                stages.add(getStepResult(stage))
            }
        }
        map[idKey] = test.resultId
        map["testId"] = test.id
        map[startTimeKey] = test.startTime
        map[endTimeKey] = test.endTime
        map["testClass"] = test.className
        map["testName"] = test.name
        map.putStatus(test.status)
        map["steps"] = stages
        return map.toMap()
    }

    private fun getStepResult(step: ReportStep): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val screenshots = mutableListOf<Map<String, Any>>()
        step.getScreenshots().forEach { screenshot ->
            screenshots.add(
                mapOf(
                    "file" to screenshot.uri.path!!,
                    "description" to screenshot.description
                )
            )
        }
        map[idKey] = step.id
        map[titleKey] = step.title
        map[startTimeKey] = step.startTime
        map[endTimeKey] = step.endTime
        map.putStatus(step.status)
        map["screenshots"] = screenshots
        return map
    }

    private fun MutableMap<String, Any>.putStatus(status: ReportStatus) {
        this[statusKey] = status.name.lowercase()
    }

    private fun Map<String, Any>.toJson() = toJsonElement().toString()

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
            else -> JsonPrimitive(toString())
        }
    }

}
