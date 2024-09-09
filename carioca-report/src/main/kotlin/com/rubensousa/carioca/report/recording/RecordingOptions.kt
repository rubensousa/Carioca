package com.rubensousa.carioca.report.recording

/**
 * See more information [here](https://developer.android.com/tools/adb#screenrecord)
 *
 * @param enabled true if screen recording should start for the test. Default: true
 * @param bitrate the bitrate of the video file. Default: 16 mbps
 * @param resolutionScale the video scale in relation to the original display size.
 * Default: 75% of the screen resolution
 * @param keepOnSuccess true if the recording should be kept if the test passes, false if it should be deleted
 * @param stopDelay the minimum amount of time to wait before the recording should be stopped. Default: 1 second
 */
data class RecordingOptions(
    val enabled: Boolean = true,
    val bitrate: Int = 16_000_000,
    val resolutionScale: Float = 0.75f,
    val keepOnSuccess: Boolean = false,
    val stopDelay: Long = 1000L,
) {
    init {
        require(resolutionScale > 0 && resolutionScale <= 1) {
            "scale must be greater than 0 and smaller or equal than 1"
        }
    }
}
