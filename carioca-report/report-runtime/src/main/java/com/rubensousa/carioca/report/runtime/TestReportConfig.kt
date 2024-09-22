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

/**
 * The configuration built from the [TestReport] annotation
 *
 * This will be used to fill the report with extra properties
 */
data class TestReportConfig(
    /**
     * @see TestReport.id
     */
    val id: String?,
    /**
     * @see TestReport.title
     */
    val title: String?,
    /**
     * @see TestReport.links
     */
    val links: List<String>,
    /**
     * @see TestReport.description
     */
    val description: String?,
) {

    fun applyTo(report: StageReport) {
        if (links.isNotEmpty()) {
            report.addProperty(ReportProperty.Links, links.toList())
        }
        id?.let {
            report.addProperty(ReportProperty.Id, it)
        }
        title?.let {
            report.addProperty(ReportProperty.Title, it)
        }
        description?.let {
            report.addProperty(ReportProperty.Description, it)
        }
    }

}
