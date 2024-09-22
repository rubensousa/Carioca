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

package com.rubensousa.carioca.android.report.recording

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.android.report.storage.TestStorageProvider
import kotlin.test.Test

class ScreenRecorderTest {

    @Test
    fun testScreenRecordingProducesValidVideoFile() {
        // given
        val storageProvider = TestStorageProvider
        val screenRecorder = ScreenRecorder(
            storageProvider = storageProvider,
            taskFactory = RecordingTaskFactoryImpl()
        )

        // when
        val recording = screenRecorder.start(
            options = RecordingOptions(
                startDelay = 0L,
                scale = 1f
            ),
            outputPath = "",
            filename = "video"
        )
        screenRecorder.stop(delete = false)

        // then
        val frame = extractFrameFromVideo(recording)!!
        val displayMetrics = InstrumentationRegistry.getInstrumentation().targetContext
            .applicationContext.resources.displayMetrics
        val width = displayMetrics.widthPixels.divisibleByEight()
        val height = displayMetrics.heightPixels.divisibleByEight()
        if (width > height) {
            assertThat(frame.width).isEqualTo(width)
            assertThat(frame.height).isEqualTo(height)
        } else {
            assertThat(frame.width).isEqualTo(height)
            assertThat(frame.height).isEqualTo(width)
        }
    }

    private fun Int.divisibleByEight(): Int {
        return this / 8 * 8
    }

    private fun extractFrameFromVideo(recording: ReportRecording): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(recording.absoluteFilePath)
        val frame = retriever.frameAtTime
        retriever.release()
        return frame
    }

}
