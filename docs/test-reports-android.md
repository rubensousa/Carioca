# Android Test Reports

## Setup

Start by including the library:

```groovy
androidTestImplementation("com.rubensousa.carioca:report-android:{{ report.version }}")
```

Then create your own report rule that extends `InstrumentedReportRule`:

```kotlin
class TestReportRule : InstrumentedReportRule()
```

!!! note
    The goal of extending InstrumentedReportRule is to apply consistent report rules throughout your test suite.
    It contains settings regarding screen recording and screenshot that you can customize.

Now apply the rule in your tests:

```kotlin
class SampleTest {

    @get:Rule
    val report = TestReportRule()

}

```

!!! note
    Use different orders for your rules in case you have multiple of them and assign the lowest value to `TestReportRule`.
    This ensures that it starts before all other rules you have in your test suite. 
    Example: `@get:Rule(order = 0)` for the report rule and `order = 1` for the next rule

This basic setup will achieve this out of the box:

1. Automatic screen recordings for every test
2. Automatic screenshot when the test fails
3. Automatic dumps of the view hierarchy if the test fails

## Visualize reports

The test reports are generated automatically after running any task like `connectedDebugAndroidTest`
and can be found in `build/outputs/connected_android_test_additional_output/**/carioca-report`.

By default, those reports are in json format and are not really easily readable.
To visualize them properly, this library ships with an [Allure](https://allurereport.org/) plugin that can be used to generate test reports based on
the metadata collected through each test execution. Check it out in [this page](android-allure-plugin.md).


## Test structure 

### Test body

You can decorate your tests with individual reports for every execution step:

```kotlin linenums="1"
@Test
fun testHomeIsDisplayedAfterQuickSettings() = report {
    
    step("Open quick settings") {
        device.openQuickSettings()
        screenshot("Quick settings displayed")
    }

    step("Press home") {
        device.pressHome()
    }
    
    step("Home is displayed") {
        screenshot("Launcher displayed")
        assertLauncherIsDisplayed()
    }
        
}
```

Optionally, `Given`, `When`, `Then` statements from BDD are also available to describe your tests:

```kotlin linenums="1"
@Test
fun testHomeIsDisplayedAfterQuickSettings() = report {
    
    Given("User opens quick settings") {
        device.openQuickSettings()
        screenshot("Quick settings displayed")
    }

    When("User presses home") {
        device.pressHome()
    }
    
    Then("Home is displayed") {
        assertLauncherIsDisplayed()
    }
        
}
```

### Before and after

If you have re-usable logic in `@Before` or `@After` that you want to include in your reports,
just use the following APIs:

```kotlin linenums="1"
@Before
fun before() = report.before {
    step("Press home") {
        device.pressHome()
    }
    step("Set device orientation to natural") {
        device.setOrientationNatural()
    }
}

@After
fun after() = report.after {
    step("Unfreeze orientation") {
        device.unfreezeRotation()
    }
}
```

### Scenario

The library includes an `InstrumentedScenario` which allows you to re-use a set of steps across multiple tests:

```kotlin linenums="1"
class ClickNotification : InstrumentedScenario("Click Notification") {

    private val device = UiDevice.getInstance(
        InstrumentationRegistry.getInstrumentation()
    )

    override fun run(scope: InstrumentedStageScope) = with(scope) {
        screenshot("Before opening notifications")

        step("User opens notifications") {
            device.openNotification()
        }

        step("Wait for animation") {
            Thread.sleep(1000L)
        }

        screenshot("After opening notifications")
        
        step("Click notification") {
            device.click()
        }

        screenshot("After clicking notification")

    }

}
```

Then, in your tests, can use it like so:

```kotlin linenums="1" hl_lines="9"
@Test
fun testAppOpensHomeAfterClickingNotification() = report {
    
    step("Trigger notification") {
        sendNotificationIntent()
    }

    // Or When(ClickNotification())
    scenario(ClickNotification())

    step("Home screen is visible") {
        assertHomeScreenDisplayed()
    }

}
```

### Extra metadata

Using `@TestReport` allows you to describe your tests in more detail:

```kotlin linenums="1"
@TestReport(
    id = "TicketID",
    title = "App opens home after notification click",
    description = "Our app needs to show the home screen " +
            "whenever our notifications are clicked",
    links = [
        "https://board.issue.tracker/id",
        "https://developer.android.com/training/testing/other-components/ui-automator"
    ]
)
@Test
fun testAppOpensHomeAfterClickingNotification() = report {
    // Test body
}
```

## Recording options

To override the recording options for individual tests, use `@TestRecording`:

```kotlin linenums="1"
// Disables screen recording for this test only
@TestRecording(
    enabled = false
)
@Test
fun fastTestThatShouldFinishInLessThan1Second() {
}
```

Or also:

```kotlin linenums="1"
// Records this test in landscape mode 
// and keeps the recording file even if the test passes
@TestRecording(
    keepOnSuccess = true,
    orientation = RecordingOrientation.LANDSCAPE
)
@Test
fun testInLandscape() {
}
```

This configuration will replace the `RecordingOptions` from `InstrumentedReportRule`


## Screenshot options

To override the screenshot options for individual tests, use `@TestScreenshot`:

```kotlin linenums="1"
@TestScreenshot(
    scale = 1.0f,
    format = Bitmap.CompressFormat.PNG,
)
@Test
fun testSomething() {
}
```

This configuration will replace the `ScreenshotOptions` from `InstrumentedReportRule`
