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

package com.rubensousa.carioca.report.runtime

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description

class TestReportConfigTest {

    @get:Rule
    val descriptionInterceptorRule = DescriptionInterceptorRule()

    @TestReport(
        id = "PROJ-1",
        title = "This is a custom test title",
        description = "All properties should be visible",
        links = ["https://link.one", "https://link.two"]
    )
    @Test
    fun `all properties are retrieved from annotation`() {
        // given
        val reportConfig = getCurrentTestReportConfig()

        // then
        assertThat(reportConfig.id).isEqualTo("PROJ-1")
        assertThat(reportConfig.title).isEqualTo("This is a custom test title")
        assertThat(reportConfig.description).isEqualTo("All properties should be visible")
        assertThat(reportConfig.links).isEqualTo(listOf("https://link.one", "https://link.two"))
    }

    @TestReport(
        id = "PROJ-1",
        title = "This is a custom test title",
        description = "All properties should be visible",
        links = ["https://link.one", "https://link.two"]
    )
    @Test
    fun `properties are assigned to stage`() {
        // given
        val reportConfig = getCurrentTestReportConfig()
        val stageReport = TestStageReport()

        // when
        reportConfig.applyTo(stageReport)

        // then
        assertThat(stageReport.getProperty<String>(ReportProperty.Id))
            .isEqualTo("PROJ-1")
        assertThat(stageReport.getProperty<String>(ReportProperty.Title))
            .isEqualTo("This is a custom test title")
        assertThat(stageReport.getProperty<String>(ReportProperty.Description))
            .isEqualTo("All properties should be visible")
        assertThat(stageReport.getProperty<List<String>>(ReportProperty.Links))
            .isEqualTo(listOf("https://link.one", "https://link.two"))
    }

    @TestReport
    @Test
    fun `properties are not applied to report when they are null`() {
        // given
        val reportConfig = getCurrentTestReportConfig()
        val stageReport = TestStageReport()

        // when
        reportConfig.applyTo(stageReport)

        // then
        assertThat(stageReport.getProperties()).isEmpty()
    }

    private fun getCurrentTestReportConfig(): TestReportConfig {
        return descriptionInterceptorRule.getDescription().getTestReportConfig()!!
    }

    /**
     * Retrieves the metadata of [TestReport] from a test's [Description]
     */
    private fun Description.getTestReportConfig(): TestReportConfig? {
        val annotation = getAnnotation(TestReport::class.java)
            ?: return null
        return TestReportConfig(
            id = annotation.id.nullIfEmpty(),
            title = annotation.title.nullIfEmpty(),
            links = annotation.links.toList(),
            description = annotation.description.nullIfEmpty(),
        )
    }

    private fun String.nullIfEmpty() = takeIf { it.isNotBlank() }

}
