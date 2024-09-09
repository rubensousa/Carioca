package com.rubensousa.carioca

import com.rubensousa.carioca.report.CariocaReportRule
import com.rubensousa.carioca.report.allure.CariocaAllureReporter
import com.rubensousa.carioca.report.recording.RecordingOptions

class AllureReportRule : CariocaReportRule(
    reporter = CariocaAllureReporter(),
    recordingOptions = RecordingOptions(
        bitrate = 20_000_000,
        resolutionScale = 1.0f,
    )
)