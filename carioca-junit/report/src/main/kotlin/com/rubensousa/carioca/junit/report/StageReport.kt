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
    private var status = ReportStatus.RUNNING
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
        status = ReportStatus.PASSED
        saveEndTime()
    }

    /**
     * Marks this stage as failed
     */
    fun fail(cause: Throwable) {
        ensureStageRunning()
        failureCause = cause
        status = ReportStatus.FAILED
        saveEndTime()
    }

    /**
     * Marks this stage as skipped
     */
    fun skip() {
        ensureStageRunning()
        status = ReportStatus.SKIPPED
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
        childStages.forEach { stage ->
            stage.reset()
        }
        status = ReportStatus.RUNNING
        startTime = System.currentTimeMillis()
        endTime = startTime
        failureCause = null
        childStages.clear()
        properties.clear()
    }

    private fun ensureStageRunning() {
        require(status == ReportStatus.RUNNING) {
            "Cannot change stage in current state: $status"
        }
    }

    private fun saveEndTime() {
        endTime = System.currentTimeMillis()
    }

    override fun equals(other: Any?): Boolean {
        val equal = other?.javaClass == javaClass
                && other is StageReport
                && other.getExecutionMetadata() == getExecutionMetadata()
                && other.getStages() == childStages
                && other.getProperties() == properties

        if(!equal){
            println("Whoops")
        }
        return equal
    }

    override fun hashCode(): Int {
        var result = getExecutionMetadata().hashCode()
        result = 31 * result + childStages.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    internal fun setStartTime(time: Long) {
        startTime = time
    }

    internal fun setEndTime(time: Long) {
        endTime = time
    }

}
