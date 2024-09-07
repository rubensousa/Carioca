package com.rubensousa.carioca.report.screenshot

import android.graphics.Bitmap

/**
 * Screenshot configurable parameters
 */
object CariocaScreenshots {

    var scale = 0.5f
        private set

    var quality = 80
        private set

    var format = Bitmap.CompressFormat.JPEG
        private set

    fun setQuality(newQuality: Int) {
        require(newQuality in 1..100)
        quality = quality
    }

    fun setScale(newScale: Float) {
        require(newScale > 0f && newScale <= 1.0f)
        scale = newScale
    }

    fun setFormat(newFormat: Bitmap.CompressFormat) {
        format = newFormat
    }

}
