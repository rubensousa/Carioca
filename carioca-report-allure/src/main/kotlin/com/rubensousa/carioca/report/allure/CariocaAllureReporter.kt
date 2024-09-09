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

package com.rubensousa.carioca.report.allure

import android.net.Uri
import com.rubensousa.carioca.report.CariocaReporter
import com.rubensousa.carioca.report.stage.ReportStatus
import com.rubensousa.carioca.report.stage.ScenarioReport
import com.rubensousa.carioca.report.stage.StepReport
import com.rubensousa.carioca.report.stage.TestReport
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.BufferedOutputStream
import java.io.OutputStream

class CariocaAllureReporter : CariocaReporter {

    private val stageValue = "finished"
    private val dirName = "allure-results"

    override fun getOutputDir(report: TestReport): String {
        return dirName
    }

    override fun getReportFilename(report: TestReport): String {
        return report.executionId + "-result.json"
    }

    override fun getScreenshotName(id: String): String {
        return getAttachmentName(id)
    }

    override fun getRecordingName(id: String): String {
        return getAttachmentName(id)
    }

    private fun getAttachmentName(id: String): String {
        return "$id-attachment"
    }

    override fun writeTestReport(report: TestReport, outputStream: OutputStream) {
        val allureReport = createReport(report)
        BufferedOutputStream(outputStream).use { stream ->
            stream.write(Json.encodeToJsonElement(allureReport).toString().toByteArray())
            stream.flush()
        }
    }

    private fun createReport(report: TestReport): CariocaAllureReport {
        return CariocaAllureReport(
            uuid = report.executionId,
            historyId = report.id,
            testCaseId = report.id,
            fullName = report.className + ".${report.name}",
            links = emptyList(),
            labels = createLabels(report),
            name = report.name,
            status = getStatus(report.status),
            statusDetails = null,
            stage = stageValue,
            steps = createSteps(report),
            attachments = emptyList(),
            start = report.startTime,
            stop = report.endTime
        )
    }

    private fun createSteps(report: TestReport): List<AllureStep> {
        val steps = mutableListOf<AllureStep>()
        val stageReports = report.getStageReports()
        stageReports.forEach { stageReport ->
            if (stageReport is ScenarioReport) {
                steps.add(createScenarioStep(stageReport))
            } else if (stageReport is StepReport) {
                steps.add(createStep(stageReport))
            }
        }
        return steps
    }

    private fun createScenarioStep(report: ScenarioReport): AllureStep {
        return AllureStep(
            name = report.name,
            status = getStatus(report.status),
            statusDetails = null,
            stage = stageValue,
            attachments = emptyList(),
            start = report.startTime,
            stop = report.endTime,
            steps = report.getSteps().map { step ->
                createStep(step)
            }
        )
    }

    private fun createStep(report: StepReport): AllureStep {
        return AllureStep(
            name = report.title,
            status = getStatus(report.status),
            statusDetails = null,
            stage = stageValue,
            attachments = getAttachments(report),
            start = report.startTime,
            stop = report.endTime,
            steps = emptyList()
        )
    }

    private fun getAttachments(report: StepReport): List<AllureAttachment> {
        val attachments = mutableListOf<AllureAttachment>()
        report.getScreenshots().forEach { screenshot ->
            attachments.add(
                AllureAttachment(
                    name = screenshot.description,
                    // Ensure that the screenshot paths are relative to the same directory
                    source = screenshot.path.replace("/$dirName/", ""),
                    type = getAttachmentType(screenshot.extension)
                )
            )
        }
        return attachments
    }

    private fun getAttachmentType(extension: String): String {
        return when (extension) {
            ".png" -> "image/png"
            ".jpg" -> "image/jpg"
            ".webp" -> "image/webp"
            else -> "text"
        }
    }

    private fun getStatus(reportStatus: ReportStatus): String {
        return if (reportStatus == ReportStatus.PASSED) {
            "passed"
        } else {
            "broken"
        }
    }

    private fun createLabels(report: TestReport): List<AllureLabel> {
        return listOf(
            AllureLabel(
                name = "package",
                value = report.packageName
            ),
            AllureLabel(
                name = "testClass",
                value = report.className
            ),
            AllureLabel(
                name = "testMethod",
                value = report.name
            ),
            AllureLabel(
                name = "suite",
                value = report.className
            ),
            AllureLabel(
                name = "framework",
                value = "junit4"
            ),
            AllureLabel(
                name = "language",
                value = "kotlin"
            ),
        )
    }
}
