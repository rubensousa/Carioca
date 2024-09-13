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
abstract class CariocaStage(
    private val executionId: String = ExecutionIdGenerator.get(),
) {

    private val childStages = mutableListOf<CariocaStage>()
    private val properties = mutableMapOf<String, Any>()
    private val startTime = System.currentTimeMillis()
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
     * @return the child stages that started within this stage
     */
    fun getStages(): List<CariocaStage> = childStages.toList()

    /**
     * Adds an extra property to this report
     *
     * @param key identifier for this property
     * @param value value of this property
     */
    fun addProperty(key: PropertyKey, value: Any) {
        properties[key.id] = value
    }

    /**
     * Adds an extra property to this report
     *
     * @param key identifier for this property
     * @param value value of this property
     */
    fun addProperty(key: String, value: Any) {
        properties[key] = value
    }

    /**
     * @return collection of properties added through [addProperty]
     */
    fun getProperties(): Map<String, Any> = properties.toMap()

    /**
     * @return the property registered previously through [addProperty] or null if not found
     */
    fun <T : Any> getProperty(key: PropertyKey): T? {
        return getProperty(key.id)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getProperty(key: String): T? {
        return properties[key] as? T?
    }

    protected fun addStage(stage: CariocaStage) {
        childStages.add(stage)
    }

    protected open fun reset() {
        childStages.clear()
    }

    private fun ensureStageRunning() {
        require(status == ExecutionStatus.RUNNING) { "Cannot change stage in current state: $status" }
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

}
