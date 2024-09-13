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

package com.rubensousa.carioca.junit.report

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.test.assertFails

class StageReportTest {

    @Test
    fun `two reports do not share the same id`() {
        // given
        val report1 = createReport()
        val report2 = createReport()

        // then
        assertThat(report1.getExecutionMetadata().uniqueId)
            .isNotEqualTo(report2.getExecutionMetadata().uniqueId)
    }

    @Test
    fun `report starts with RUNNING state`() {
        // given
        val report = createReport()

        // then
        assertThat(report.getExecutionMetadata().status)
            .isEqualTo(ReportStatus.RUNNING)
    }

    @Test
    fun `report changes status to passed`() {
        // given
        val report = createReport()

        // when
        report.pass()

        // then
        assertThat(report.getExecutionMetadata().status)
            .isEqualTo(ReportStatus.PASSED)
    }

    @Test
    fun `report changes status to failed`() {
        // given
        val report = createReport()
        val cause = IllegalStateException()

        // when
        report.fail(cause)

        // then
        val metadata = report.getExecutionMetadata()

        assertThat(metadata.status).isEqualTo(ReportStatus.FAILED)
        assertThat(metadata.failureCause).isSameInstanceAs(cause)
    }

    @Test
    fun `report changes status to skipped`() {
        // given
        val report = createReport()

        // when
        report.skip()

        // then
        val metadata = report.getExecutionMetadata()
        assertThat(metadata.status).isEqualTo(ReportStatus.SKIPPED)
    }

    @Test
    fun `exception is thrown when pass is called twice`() {
        // given
        val report = createReport()
        report.pass()

        // when
        val throwable = assertFails {
            report.pass()
        }

        // then
        assertThat(throwable.message!!)
            .isEqualTo("Cannot change stage in current state: ${ReportStatus.PASSED}")
    }

    @Test
    fun `exception is thrown when fail is called twice`() {
        // given
        val report = createReport()
        val cause = IllegalStateException()
        report.fail(cause)

        // when
        val throwable = assertFails {
            report.fail(cause)
        }

        // then
        assertThat(throwable.message!!)
            .isEqualTo("Cannot change stage in current state: ${ReportStatus.FAILED}")
    }

    @Test
    fun `end time is saved when report is successful`() {
        // given
        val report = createReport()
        val startTime = report.getExecutionMetadata().startTime
        val delay = 200L
        Thread.sleep(delay)

        // when
        report.pass()

        // then
        assertThat(report.getExecutionMetadata().endTime >= startTime + delay).isTrue()
    }

    @Test
    fun `end time is saved when report fails`() {
        // given
        val report = createReport()
        val delay = 200L
        val startTime = report.getExecutionMetadata().startTime
        val cause = IllegalStateException()
        Thread.sleep(delay)

        // when
        report.fail(cause)

        // then
        assertThat(report.getExecutionMetadata().endTime >= startTime + delay).isTrue()
    }

    @Test
    fun `property is saved`() {
        // given
        val report = createReport()
        val propertyKey = ReportProperty.Title
        val propertyValue = "test title"

        // when
        report.addProperty(propertyKey, propertyValue)

        // then
        assertThat(report.getProperty<String>(propertyKey)).isEqualTo(propertyValue)
    }

    @Test
    fun `property is null if it was not saved`() {
        // given
        val report = createReport()

        // then
        assertThat(report.getProperty<String>(ReportProperty.Title)).isNull()
    }

    @Test
    fun `property is null if it does not match saved type`() {
        // given
        val report = createReport()

        // when
        report.addProperty(ReportProperty.Title, 1)

        // then
        val value = report.getProperty<String>(ReportProperty.Title)
        assertThat(value).isNull()
    }

    @Test
    fun `nested stages are added to the report`() {
        // given
        val report = createReport()
        val childReport1 = createReport()
        val childReport2 = createReport()

        // when
        report.addStage(childReport1)
        report.addStage(childReport2)

        // then
        assertThat(report.getStages())
            .isEqualTo(listOf(childReport1, childReport2))
    }

    @Test
    fun `reset clears every metadata`() {
        // given
        val report = createReport()
        Thread.sleep(100L)
        val cause = IllegalStateException()
        report.fail(cause)
        report.addProperty(ReportProperty.Title, "title")
        report.addStage(createReport())

        // when
        report.reset()

        // then
        val metadata = report.getExecutionMetadata()
        assertThat(metadata.startTime).isEqualTo(metadata.endTime)
        assertThat(metadata.status).isEqualTo(ReportStatus.RUNNING)
        assertThat(metadata.failureCause).isNull()
        assertThat(report.getProperties()).isEmpty()
        assertThat(report.getStages()).isEmpty()
    }

    @Test
    fun `equals and hashcode`() {
        // given
        val childStage = TestStageReport(2)
        val cause = IllegalStateException()
        val firstReport = TestStageReport(1)
        firstReport.fail(cause)
        val secondReport = TestStageReport(1)
        secondReport.fail(cause)
        secondReport.setStartTime(firstReport.getExecutionMetadata().startTime)
        secondReport.setEndTime(firstReport.getExecutionMetadata().endTime)

        // when
        val reports = listOf(firstReport, secondReport)
        reports.forEach { report ->
            report.addStage(childStage)
            report.addProperty(ReportProperty.Title, "title")
        }

        // then
        assertThat(firstReport).isEqualTo(secondReport)
        assertThat(firstReport.hashCode()).isEqualTo(secondReport.hashCode())
    }

    private fun createReport(): TestStageReport = TestStageReport()

}
