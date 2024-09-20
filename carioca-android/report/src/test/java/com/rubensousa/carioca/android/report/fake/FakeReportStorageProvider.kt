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

package com.rubensousa.carioca.android.report.fake

import android.net.Uri
import com.rubensousa.carioca.android.report.storage.ReportStorageProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.io.path.createParentDirectories

class FakeReportStorageProvider : ReportStorageProvider {

    private val localDir = File(".").absoluteFile.parentFile

    // Create a fake local storage for testing
    private val testDir = File(localDir, "test-files")

    val uris = mutableMapOf<String, Uri>()
    val filesSaved = mutableMapOf<String, File>()

    var lastFile: File? = null

    init {
        testDir.mkdirs()
    }

    override fun getOutputDir(): File {
        return testDir
    }

    override fun getOutputStream(path: String): OutputStream {
        val file = File(testDir, path)
        filesSaved[path] = file
        file.toPath().createParentDirectories()
        lastFile = file
        return FileOutputStream(file)
    }

    override fun getOutputUri(path: String): Uri {
        return uris[path]!!
    }

    override fun delete(path: String) {
        filesSaved.remove(path)?.delete()
    }

    override fun deleteTemporaryFiles() {
        filesSaved.clear()
        testDir.deleteRecursively()
    }

    fun clean() {
        testDir.deleteRecursively()
    }

}

