package com.rubensousa.carioca.report

import com.rubensousa.carioca.report.stage.TestSuiteReport
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
 * Represents the test report in a json format with the following structure:
 *
 * ```
 * {
 *  "executionId": "bcd2d033-ecf9-4f66-b63c-399385cb63b8",
 *  "testId": "This is a persistent test id",
 *  "startTime": 1725741327159,
 *  "endTime": 1725741327621,
 *  "testClass": "com.rubensousa.carioca.SampleReportTest",
 *  "testName": "testSomethingHappens",
 *  "status": "passed",
 *  "stages": [
 *      {
 *        "type": "scenario",
 *        "properties": {
 *          "executionId": "25e88325-29ed-45d4-8440-edad04568bdf",
 *          "startTime": 1725741327161,
 *          "endTime": 1725741327162,
 *          "scenarioId": "Sample screen Scenario",
 *          "steps": [
 *            {
 *              "type": "step",
 *              "properties": {
 *                "executionId": "ac61c219-6aa3-405b-bf90-a4e1be45738d",
 *                "title": "Step 1 of Scenario",
 *                "startTime": 1725741327161,
 *                "endTime": 1725741327161,
 *                "status": "passed",
 *                "screenshots": null
 *              }
 *            }
 *          ]
 *        }
 *      },
 *      {
 *        "type": "step",
 *        "properties": {
 *          "executionId": "f0bf1745-c24f-4985-9e16-01566d5dac3b",
 *          "title": "Setup some state",
 *          "startTime": 1725741327162,
 *          "endTime": 1725741327442,
 *          "status": "passed",
 *          "screenshots": [
 *            {
 *              "file": "/carioca_report/com.rubensousa.carioca.SampleReportTest/testSomethingHappens/screenshots/2c7b0f84-9102-438e-babd-4001d9d27709.jpg",
 *              "description": "Save this"
 *            }
 *          ]
 *        }
 *      },
 *    ]
 * }
 * ```
 */
class JsonReporter : CariocaReporter {

    override val filename: String
        get() = "carioca_report.json"

    private val startTimeKey = "startTime"
    private val endTimeKey = "endTime"
    private val executionIdKey = "executionId"
    private val titleKey = "title"
    private val statusKey = "status"

    override fun writeTestReport(report: TestReport, outputStream: OutputStream) {
        val metadata = getTestResult(report)
        serialize(metadata.toJson(), outputStream)
    }

    override fun writeTestSuiteReport(report: TestSuiteReport, outputStream: OutputStream) {
        val metadata = getSuiteResult(report)
        serialize(metadata.toJson(), outputStream)
    }

    private fun serialize(json: String, outputStream: OutputStream) {
        BufferedOutputStream(outputStream).use { stream ->
            stream.write(json.toByteArray())
            stream.flush()
        }
    }

    private fun getSuiteResult(report: TestSuiteReport): Map<String, Any?> {
        val output = mutableMapOf<String, Any?>()
        output[executionIdKey] = report.id
        output[startTimeKey] = report.startTime
        output[endTimeKey] = report.endTime
        output.putStatus(report.status)
        return output
    }

    private fun getTestResult(test: TestReport): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val stages = mutableListOf<Map<String, Any>>()
        test.getStages().forEach { stage ->
            if (stage is ScenarioReport) {
                stages.add(getScenarioResult(stage))
            } else if (stage is StepReport) {
                stages.add(getStepResult(stage))
            }
        }
        map[executionIdKey] = test.resultId
        map["testId"] = test.id
        map[startTimeKey] = test.startTime
        map[endTimeKey] = test.endTime
        map["testClass"] = test.className
        map["testName"] = test.name
        map.putStatus(test.status)
        map["stages"] = stages
        return map
    }

    private fun getScenarioResult(scenario: ScenarioReport): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val steps = mutableListOf<Map<String, Any>>()
        scenario.getSteps().forEach { step ->
            steps.add(getStepResult(step))
        }
        map[executionIdKey] = scenario.resultId
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
                    "file" to screenshot.uri.path!!,
                    "description" to screenshot.description
                )
            )
        }
        map[executionIdKey] = step.id
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
