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


/**
 * The building block of all test reports
 */
abstract class StageReport(
    private val executionId: String = ExecutionIdGenerator.get(),
) {

    private val childStages = mutableListOf<StageReport>()
    private val properties = mutableMapOf<ReportProperty, Any>()
    private var startTime = System.currentTimeMillis()
    private var endTime = startTime
    private var status = ExecutionStatus.RUNNING
    private var failureCause: Throwable? = null

    /**
     * @return the execution metadata associated to this stage
     */
    fun getExecutionMetadata(): ExecutionMetadata {
        return ExecutionMetadata(
            uniqueId = executionId,
            failureCause = failureCause,
            status = status,
            startTime = startTime,
            endTime = endTime
        )
    }

    /**
     * Marks this stage as passed
     */
    fun pass() {
        ensureStageRunning()
        status = ExecutionStatus.PASSED
        saveEndTime()
    }

    /**
     * Marks this stage as failed
     */
    fun fail(cause: Throwable) {
        ensureStageRunning()
        failureCause = cause
        status = ExecutionStatus.FAILED
        saveEndTime()
    }

    /**
     * Marks this stage as skipped
     */
    fun skip() {
        ensureStageRunning()
        status = ExecutionStatus.SKIPPED
    }

    /**
     * Adds an extra property to this report
     *
     * @param key identifier for this property
     * @param value value of this property
     */
    fun addProperty(key: ReportProperty, value: Any) {
        properties[key] = value
    }

    /**
     * @return all properties registered through [addProperty]
     */
    fun getProperties() = properties.toMap()

    /**
     * @return the property registered previously through [addProperty]
     * or null if not found or does not match the expected type
     */
    inline fun <reified T> getProperty(key: ReportProperty): T? {
        return getProperties()[key] as? T?
    }

    /**
     * @return the child stages that started within this stage
     */
    fun getStages(): List<StageReport> = childStages.toList()

    /**
     * @param stage a nested stage within this report
     */
    fun addStage(stage: StageReport) {
        childStages.add(stage)
    }

    open fun reset() {
        status = ExecutionStatus.RUNNING
        startTime = System.currentTimeMillis()
        endTime = startTime
        failureCause = null
        childStages.clear()
        properties.clear()
    }

    private fun ensureStageRunning() {
        require(status == ExecutionStatus.RUNNING) {
            "Cannot change stage in current state: $status"
        }
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

}
