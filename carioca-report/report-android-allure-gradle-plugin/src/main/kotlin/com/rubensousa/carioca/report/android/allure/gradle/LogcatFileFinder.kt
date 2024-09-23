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

package com.rubensousa.carioca.report.android.allure.gradle

import java.io.File

class LogcatFileFinder {

    private val filePrefix = "logcat-"

    fun find(dir: File): Map<String, File> {
        val logDirectory = findDirWithLogs(dir) ?: return emptyMap()
        val files = mutableMapOf<String, File>()
        logDirectory.listFiles()?.forEach { file ->
            if (isLogcatFile(file)) {
                files[extractTestFullName(file)] = file
            }
        }
        return files
    }

    private fun findDirWithLogs(dir: File): File? {
        val dirs = ArrayDeque<File>()
        dirs.addLast(dir)
        while (dirs.isNotEmpty()) {
            val currentDir = dirs.removeLast()
            currentDir.listFiles()?.forEach { file ->
                if (isLogcatFile(file)) {
                    return currentDir
                } else if (file.isDirectory) {
                    dirs.addLast(file)
                }
            }
        }
        return null
    }

    private fun isLogcatFile(file: File): Boolean {
        return file.name.startsWith(filePrefix)
    }

    private fun extractTestFullName(file: File): String {
        return file.nameWithoutExtension.replace(filePrefix, "")
            .replace("-", ".")
    }

}
