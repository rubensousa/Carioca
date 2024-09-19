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
 * The building block of all test reports
 *
 * To get the main result of this report, use [getExecutionMetadata]
 *
 * To get the reports within this own report, use:
 * [getStagesBefore], [getTestStages], [getStagesAfter]
 */
abstract class StageReport(
    val executionId: String = ExecutionIdGenerator.get(),
) {

    private val attachments = mutableListOf<StageAttachment>()
    private val beforeStages = mutableListOf<StageReport>()
    private val testStages = mutableListOf<StageReport>()
    private val afterStages = mutableListOf<StageReport>()
    private val properties = mutableMapOf<ReportProperty, Any>()
    private var startTime = System.currentTimeMillis()
    private var endTime = startTime
    private var status = ReportStatus.RUNNING
    private var failureCause: Throwable? = null

    /**
     * @param attachment attachment to be deleted from the filesystem
     */
    abstract fun deleteAttachment(attachment: StageAttachment)

    /**
     * @return the type of this stage. Can be "test", "step", or anything else
     */
    abstract fun getType(): String

    /**
     * @return the title of this stage
     */
    abstract fun getTitle(): String

    /**
     * @return an optional identifier for this stage
     */
    open fun getId(): String {
        return getProperty<String>(ReportProperty.Id) ?: executionId
    }

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
        // Delete all attachments that shouldn't be kept when the test passes
        getAttachments().forEach { attachment ->
            if (!attachment.keepOnSuccess) {
                deleteAttachment(attachment)
                detach(attachment)
            }
        }
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
     * @return the child stages that started in a `@Before` method
     */
    fun getStagesBefore(): List<StageReport> = beforeStages.toList()

    /**
     * @return the child stages that started within this stage inside the test method
     */
    fun getTestStages(): List<StageReport> = testStages.toList()

    /**
     * @return the child stages that started in a `@After` method
     */
    fun getStagesAfter(): List<StageReport> = afterStages.toList()

    /**
     * @param stage a nested stage within this report
     */
    fun addStageBefore(stage: StageReport) {
        beforeStages.add(stage)
    }

    /**
     * @param stage a nested stage within this report
     */
    fun addTestStage(stage: StageReport) {
        testStages.add(stage)
    }

    /**
     * @param stage a nested stage within this report
     */
    fun addStageAfter(stage: StageReport) {
        afterStages.add(stage)
    }

    /**
     * @param attachment an attachment that will be linked to this report
     */
    fun attach(attachment: StageAttachment) {
        attachments.add(attachment)
    }

    /**
     * @param attachment the attachment to remove from this report
     */
    fun detach(attachment: StageAttachment) {
        attachments.remove(attachment)
    }

    /**
     * @return all attachments registered through [attach]
     */
    fun getAttachments(): List<StageAttachment> = attachments.toList()

    /**
     * Clears all the metadata associated to this report
     */
    open fun reset() {
        getAttachments().forEach { attachment ->
            deleteAttachment(attachment)
        }
        testStages.forEach { stage -> stage.reset() }
        beforeStages.forEach { stage -> stage.reset() }
        afterStages.forEach { stage -> stage.reset() }
        status = ReportStatus.RUNNING
        startTime = System.currentTimeMillis()
        endTime = startTime
        failureCause = null
        testStages.clear()
        beforeStages.clear()
        afterStages.clear()
        properties.clear()
        attachments.clear()
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
        return other?.javaClass == javaClass
                && other is StageReport
                && other.getId() == getId()
                && other.executionId == executionId
                && other.getExecutionMetadata() == getExecutionMetadata()
                && other.getTestStages() == testStages
                && other.getStagesBefore() == beforeStages
                && other.getStagesAfter() == afterStages
                && other.getProperties() == properties
                && other.getAttachments() == attachments
    }

    override fun hashCode(): Int {
        var result = getExecutionMetadata().hashCode()
        result = 31 * result + testStages.hashCode()
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
