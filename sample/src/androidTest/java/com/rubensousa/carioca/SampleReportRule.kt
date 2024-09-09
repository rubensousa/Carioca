package com.rubensousa.carioca

import com.rubensousa.carioca.report.CariocaReportRule
import com.rubensousa.carioca.report.allure.CariocaAllureReporter
import com.rubensousa.carioca.report.recording.RecordingOptions
import com.rubensousa.carioca.report.screenshot.ScreenshotOptions

class SampleReportRule : CariocaReportRule(
    reporter = CariocaAllureReporter(),
    recordingOptions = RecordingOptions(
        bitrate = 20_000_000,
        resolutionScale = 1.0f,
        /**
         * Be extra careful with this option,
         * as this might fill up the entire device depending on the number of tests.
         * For demo purposes, we have it on
         */
        keepOnSuccess = true
    ),
    screenshotOptions = ScreenshotOptions(
        scale = 1f
    )
)