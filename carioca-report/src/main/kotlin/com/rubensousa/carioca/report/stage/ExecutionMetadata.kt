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

/**
 * Metadata for the execution for a test, step or scenario
 *
 * @param uniqueId the unique identifier for the stage. It changes across different executions
 * @param failureCause if the stage failed, this contains the error thrown
 * @param status the execution status of this stage
 * @param startTime indicates when this stage started its execution
 * @param endTime indicates when this stage stopped its execution
 */
data class ExecutionMetadata(
    val uniqueId: String,
    val failureCause: Throwable?,
    val status: ExecutionStatus,
    val startTime: Long,
    val endTime: Long,
)
