package com.rubensousa.carioca

import android.os.health.UidHealthStats
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.rubensousa.carioca.report.annotations.TestId
import org.junit.Rule
import org.junit.Test

class SampleTest {

    @get:Rule
    val reportRule = AllureReportRule()

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @TestId(id = "This is a persistent test id")
    @Test
    fun testSomethingHappens() = reportRule.report {
        scenario(SampleScreenScenario())

        step("Open notification") {
            device.openNotification()
            screenshot("Notification bar visible")
        }
        step("Open quick settings") {
            device.openQuickSettings()
            sampleScreen {
                assertIsDisplayed()
                screenshot("Quick settings displayed")
            }
        }
        step("Press home") {
            device.pressHome()
            sampleScreen {
                assertIsNotDisplayed()
                screenshot("Launcher")
            }
        }
    }

}
