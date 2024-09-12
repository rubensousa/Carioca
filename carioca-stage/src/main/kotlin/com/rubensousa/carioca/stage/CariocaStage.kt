package com.rubensousa.carioca.stage

/**
 * The basic contract for all stages
 *
 * Check [ExecutionMetadata] for more details
 */
interface CariocaStage {

    /**
     * @return the execution metadata associated to this stage
     */
    fun getExecutionMetadata(): ExecutionMetadata

    /**
     * @return the child stages that started within this stage
     */
    fun getStages(): List<CariocaStage>

    /**
     * Marks this stage as passed
     */
    fun pass()

    /**
     * Marks this stage as failed
     */
    fun fail(cause: Throwable)

    /**
     * Marks this stage as skipped
     */
    fun skip()

}
