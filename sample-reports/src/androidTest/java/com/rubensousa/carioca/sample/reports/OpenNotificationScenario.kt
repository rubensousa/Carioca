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

package com.rubensousa.carioca.sample.reports

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.android.stage.InstrumentedScenario
import com.rubensousa.carioca.report.android.stage.InstrumentedStageScope

class OpenNotificationScenario : InstrumentedScenario() {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    override fun getTitle(): String {
        return "Open Notification"
    }

    override fun run(scope: InstrumentedStageScope) = with(scope) {
        screenshot("Before opening notifications")

        step("Request notification open") {
            device.openNotification()
        }

        step("Wait for animation") {
            Thread.sleep(1000L)
            screenshot("Notification")
        }

        screenshot("After opening notifications")
    }

}
