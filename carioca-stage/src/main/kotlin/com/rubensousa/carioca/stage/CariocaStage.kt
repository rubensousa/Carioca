package com.rubensousa.carioca.stage

/**
 * The basic contract for all stages: execution metadata
 *
 * Check [ExecutionMetadata] for more details
 */
interface CariocaStage {

    /**
     * @return the execution metadata associated to this stage
     */
    fun getExecutionMetadata(): ExecutionMetadata

}
