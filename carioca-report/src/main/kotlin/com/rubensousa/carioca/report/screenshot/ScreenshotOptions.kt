package com.rubensousa.carioca.report.screenshot

import android.graphics.Bitmap

data class ScreenshotOptions(
    val scale: Float = 0.5f,
    val quality: Int = 80,
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
) {

    init {
        require(quality in 1..100)
        require(scale > 0f && scale <= 1.0f)
    }

}
