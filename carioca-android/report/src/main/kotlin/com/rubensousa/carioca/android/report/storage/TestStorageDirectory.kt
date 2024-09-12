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

package com.rubensousa.carioca.android.report.storage

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

/**
 * Adapted from: androidx.test.platform.io.TestDirCalculator to ensure we can delete files
 */
@SuppressLint("SdCardPath")
internal object TestStorageDirectory {

    val outputDir: File by lazy { calculateOutputDir() }

    private fun calculateOutputDir(): File {
        val additionalOutputTestDir = try {
            InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
        } catch (exception: Exception) {
            null
        }
        if (additionalOutputTestDir != null) {
            return File(additionalOutputTestDir)
        }
        return File(calculateDefaultRootDir(), "additionalTestOutputDir")
    }

    private fun calculateDefaultRootDir(): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (Build.VERSION.SDK_INT >= 29) {
            // On Android Q+ first attempt to use the media directory because that is
            // writable without any extra storage permissions
            // https://developer.android.com/about/versions/11/privacy/storage
            @Suppress("DEPRECATION")
            for (mediaDir in context.externalMediaDirs) {
                if (Environment.getExternalStorageState(mediaDir) == Environment.MEDIA_MOUNTED) {
                    return mediaDir
                }
            }
        }
        // on older platforms or if media dir wasn't mounted try using the app's external cache dir
        if (context.externalCacheDir != null) {
            return context.externalCacheDir!!
        }
        // finally, fallback to cacheDir
        return context.cacheDir
    }

}
