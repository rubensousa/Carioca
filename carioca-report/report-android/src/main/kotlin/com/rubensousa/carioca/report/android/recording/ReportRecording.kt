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

import java.io.File

/**
 * A recording taken during a test report
 *
 * @param absoluteFilePath the absolute file path to the video recording
 * @param relativeFilePath the relative file path inside the test storage directory
 * @param filename the filename of the video recording file
 * @param tmpFile the temporary file of the recording, while it is being recorded
 */
data class ReportRecording(
    val absoluteFilePath: String,
    val relativeFilePath: String,
    val filename: String,
    val tmpFile: File,
)
