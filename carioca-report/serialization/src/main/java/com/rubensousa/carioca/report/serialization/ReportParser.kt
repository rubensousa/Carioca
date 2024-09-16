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

package com.rubensousa.carioca.report.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class ReportParser {

    fun parseSuite(inputDir: File): SuiteReport? {
        val reportDir = findReportDir(inputDir) ?: return null
        reportDir.listFiles()?.forEach { file ->
            if (file.name == ReportFiles.SUITE_REPORT) {
                return decodeFromFile(file)
            }
        }
        return null
    }

    fun parseTests(inputDir: File): List<TestReport> {
        val reportDir = findReportDir(inputDir) ?: return emptyList()
        val tests = mutableListOf<TestReport>()
        reportDir.listFiles()?.forEach { file ->
            if (file.name.endsWith(ReportFiles.TEST_REPORT)) {
                val test = decodeFromFile<TestReport>(file)
                if (test != null) {
                    tests.add(test)
                }
            }
        }
        return tests
    }

    /**
     * Should be future proof, as we find the directory with our own name,
     * without assuming the parent directory structure
     */
    fun findReportDir(inputDir: File): File? {
        if (inputDir.name == ReportFiles.REPORT_DIR) {
            return inputDir
        }
        inputDir.listFiles()?.forEach { dir ->
            return findReportDir(dir)
        }
        return null
    }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> decodeFromFile(file: File): T? {
        return try {
            file.inputStream().use {
                Json.decodeFromStream(it)
            }
        } catch (exception: Exception) {
            null
        }
    }

}
