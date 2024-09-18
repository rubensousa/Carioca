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

package com.rubensousa.carioca.report.junit4

import com.rubensousa.carioca.report.core.TestReport
import com.rubensousa.carioca.report.core.TestReportConfig
import org.junit.runner.Description

/**
 * Retrieves the metadata of [TestReport] from a test's [Description]
 */
fun Description.getTestReportConfig(): TestReportConfig? {
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
