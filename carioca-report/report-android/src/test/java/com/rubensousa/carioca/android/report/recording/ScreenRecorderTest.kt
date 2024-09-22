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

import com.google.common.truth.Truth.assertThat
import com.rubensousa.carioca.report.android.TemporaryStorageRule
import com.rubensousa.carioca.report.android.fake.FakeReportStorageProvider
import org.junit.Rule
import org.junit.Test

class ScreenRecorderTest {

    @get:Rule
    val temporaryStorageRule = TemporaryStorageRule()

    private val storageProvider = FakeReportStorageProvider()
    private val recorder = ScreenRecorder(
        storageProvider = storageProvider,
        taskFactory = object : RecordingTaskFactory {
            override fun create(recording: ReportRecording, options: RecordingOptions): RecordingTask {
                return FakeRecordingTask(recording, storageProvider)
            }
        }
    )

    @Test
    fun `test recording starts with expected temporary file`() {
        // given
        val options = RecordingOptions()
        val path = "/dir"
        val filename = "2312-2312-1232-video"

        // when
        recorder.start(
            options = options,
            outputPath = path,
            filename = filename
        )

        // then
        val file = storageProvider.lastFile!!
        assertThat(file.name).isEqualTo("tmp_$filename.mp4")
    }

    @Test
    fun `test recording starts with expected metadata`() {
        // given
        val options = RecordingOptions()
        val path = "dir"
        val filename = "2312-2312-1232-video"

        // when
        val recording = recorder.start(
            options = options,
            outputPath = "/$path",
            filename = filename
        )

        // then
        val expectedOutputDir = storageProvider.getOutputDir()
        val expectedFilename = "$filename.mp4"
        val expectedRelativePath = "/$path/$expectedFilename"
        val expectedAbsolutePath = "${expectedOutputDir.absolutePath}/$path/$expectedFilename"
        assertThat(recording.filename).isEqualTo(expectedFilename)
        assertThat(recording.relativeFilePath).isEqualTo(expectedRelativePath)
        assertThat(recording.absoluteFilePath).isEqualTo(expectedAbsolutePath)
        assertThat(recording.tmpFile.name).isEqualTo("tmp_$expectedFilename")
    }

    @Test
    fun `repeated start cancels previous task and deletes its file`() {
        // given
        val firstRecording = recorder.start(
            options = RecordingOptions(),
            outputPath = "",
            filename = "video1"
        )
        val firstRecordingFile = firstRecording.tmpFile

        // when
        val secondRecording = recorder.start(
            options = RecordingOptions(),
            outputPath = "",
            filename = "video2"
        )
        val secondRecordingFile = secondRecording.tmpFile

        // then
        assertThat(firstRecordingFile.exists()).isFalse()
        assertThat(secondRecordingFile.exists()).isTrue()
    }

    @Test
    fun `stop deletes the file when delete is requested`() {
        // given
        val recording = recorder.start(
            options = RecordingOptions(),
            outputPath = "",
            filename = "video1"
        )

        // when
        recorder.stop(delete = true)

        // then
        assertThat(recording.tmpFile.exists()).isFalse()
    }

    @Test
    fun `stop moves the file to the report storage final location`() {
        // given
        val recording = recorder.start(
            options = RecordingOptions(),
            outputPath = "",
            filename = "video1"
        )

        // when
        recorder.stop(delete = false)

        // then
        val storageFile = storageProvider.filesSaved[recording.relativeFilePath]!!
        assertThat(storageFile.exists()).isTrue()
        assertThat(recording.tmpFile.exists()).isFalse()
    }


}
