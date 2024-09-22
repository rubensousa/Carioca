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

package com.rubensousa.carioca.report.android.recording

/**
 * Signals a test to be screen recorded
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TestRecording(
    /**
     * @see RecordingOptions.enabled
     */
    val enabled: Boolean = true,
    /**
     * @see RecordingOptions.bitrate
     */
    val bitrate: Int = 16_000_000,
    /**
     * @see RecordingOptions.scale
     */
    val scale: Float = 0.75f,
    /**
     * @see RecordingOptions.keepOnSuccess
     */
    val keepOnSuccess: Boolean = false,
    /**
     * @see RecordingOptions.startDelay
     */
    val startDelay: Long = 1000L,
    /**
     * @see RecordingOptions.stopDelay
     */
    val stopDelay: Long = 1000L,
    /**
     * @see RecordingOptions.continueDelay
     */
    val continueDelay: Long = 500L,
)
