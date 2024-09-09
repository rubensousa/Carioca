package com.rubensousa.carioca.report.recording

data class ScreenRecording(
    val absoluteFilePath: String,
    val relativeFilePath: String,
    val filename: String,
    val options: RecordingOptions,
)
