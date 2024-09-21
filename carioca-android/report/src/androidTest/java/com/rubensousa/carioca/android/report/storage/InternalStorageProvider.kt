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

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.createParentDirectories

class InternalStorageProvider : ReportStorageProvider {

    private val context = InstrumentationRegistry.getInstrumentation().context
    private val dir = File(context.filesDir, "local_storage")

    init {
        dir.mkdirs()
    }

    override fun getOutputStream(path: String): OutputStream {
        val file = createFile(path)
        return FileOutputStream(file)
    }

    override fun getInputStream(path: String): InputStream {
        return FileInputStream(createFile(path))
    }

    override fun getOutputUri(path: String): Uri {
        return Uri.fromFile(createFile(path))
    }

    override fun delete(path: String) {
        createFile(path).delete()
    }

    override fun deleteTemporaryFiles() {
        dir.deleteRecursively()
    }

    override fun getOutputDir(): File {
        return dir
    }

    private fun createFile(path: String): File {
        val file = File(dir, path)
        file.toPath().createParentDirectories()
        return file
    }
}