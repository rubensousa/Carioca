package com.rubensousa.carioca

import com.rubensousa.carioca.report.CariocaReportRule
import com.rubensousa.carioca.report.allure.CariocaAllureReporter

class AllureReportRule : CariocaReportRule(CariocaAllureReporter())