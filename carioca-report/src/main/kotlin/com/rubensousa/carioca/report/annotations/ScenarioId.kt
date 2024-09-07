package com.rubensousa.carioca.report.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ScenarioId(val id: String)
