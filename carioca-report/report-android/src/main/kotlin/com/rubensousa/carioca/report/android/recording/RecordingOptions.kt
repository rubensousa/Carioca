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

import org.junit.runner.Description

/**
 * See more information [here](https://developer.android.com/tools/adb#screenrecord)
 *
 * @param enabled true if screen recording should start for the test. Default: true
 * @param bitrate the bitrate of the video file. Default: 16 mbps
 * @param scale the video scale in relation to the original display size.
 * Default: 75% of the screen resolution
 * @param keepOnSuccess true if the recording should be kept if the test passes,
 * false if it should be deleted
 * @param startDelay the minimum amount of time to wait after the recording starts.
 * Default: 500ms
 * @param stopDelay the minimum amount of time to wait before the recording should be stopped.
 * Default: 1 second
 * @param continueDelay the minimum amount of time to wait
 * before continuing to the recording of a next test
 * Default: 250ms
 * @param orientation the orientation of the screen recording.
 * Default: [RecordingOrientation.NATURAL]
 */
data class RecordingOptions(
    val enabled: Boolean = true,
    val bitrate: Int = 16_000_000,
    val scale: Float = 0.75f,
    val keepOnSuccess: Boolean = false,
    val startDelay: Long = 500L,
    val stopDelay: Long = 1000L,
    val continueDelay: Long = 250L,
    val orientation: RecordingOrientation = RecordingOrientation.NATURAL,
) {
    init {
        require(scale > 0 && scale <= 1) {
            "scale must be greater than 0 and smaller or equal than 1"
        }
    }


    companion object {

        fun from(description: Description): RecordingOptions? {
            val annotation = description.getAnnotation(TestRecording::class.java)
                ?: return null
            return RecordingOptions(
                enabled = annotation.enabled,
                bitrate = annotation.bitrate,
                scale = annotation.scale,
                keepOnSuccess = annotation.keepOnSuccess,
                startDelay = annotation.startDelay,
                stopDelay = annotation.stopDelay,
                continueDelay = annotation.continueDelay,
                orientation = annotation.orientation
            )
        }

    }

}
