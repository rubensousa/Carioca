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

package com.rubensousa.carioca.report.interceptor

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.stage.TestReport
import org.junit.runner.Description

class DumpHierarchyInterceptor : CariocaInterceptor {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val file = "view_hierarchy.txt"

    override fun onTestFailed(report: TestReport, error: Throwable, description: Description) {
        super.onTestFailed(report, error, description)
        try {
            val request = report.createAttachment(
                filename = file,
                description = "View hierarchy dump",
                mimeType = "text/plain"
            )
            device.dumpWindowHierarchy(request.outputStream)
            report.attach(request)
        } catch (exception: Exception) {
            // Do nothing
        }
    }

}
