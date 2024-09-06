package com.rubensousa.carioca.core

import android.graphics.Bitmap

/**
 * Screenshot configurable parameters
 */
object CariocaScreenshots {

    var scale = 1f
        private set

    var quality = 90
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
