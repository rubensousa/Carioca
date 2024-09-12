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

import com.rubensousa.carioca.report.storage.IdGenerator

/**
 * A data structure that is used by all stages.
 */
abstract class AbstractStage : InstrumentedStage {

    private val executionId = IdGenerator.get()
    private val startTime = System.currentTimeMillis()
    private var endTime = startTime
    private var status = ExecutionStatus.RUNNING
    private var failureCause: Throwable? = null

    override fun getExecutionMetadata(): ExecutionMetadata {
        return ExecutionMetadata(
            uniqueId = executionId,
            failureCause = failureCause,
            status = status,
            startTime = startTime,
            endTime = endTime
        )
    }

    internal fun pass() {
        status = ExecutionStatus.PASSED
        saveEndTime()
    }

    internal fun fail(cause: Throwable) {
        failureCause = cause
        status = ExecutionStatus.FAILED
        saveEndTime()
    }

    internal fun skip() {
        status = ExecutionStatus.SKIPPED
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

}
