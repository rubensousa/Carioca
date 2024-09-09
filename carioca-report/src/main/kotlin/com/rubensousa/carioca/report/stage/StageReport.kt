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

package com.rubensousa.carioca.report.stage

import com.rubensousa.carioca.report.internal.IdGenerator

abstract class StageReport(val id: String) {

    var status = ReportStatus.SKIPPED
        private set

    var startTime = System.currentTimeMillis()
        private set

    var endTime = startTime
        private set

    val executionId = IdGenerator.get()

    internal fun pass() {
        status = ReportStatus.PASSED
        saveEndTime()
    }

    internal fun fail() {
        status = ReportStatus.FAILED
        saveEndTime()
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

}
