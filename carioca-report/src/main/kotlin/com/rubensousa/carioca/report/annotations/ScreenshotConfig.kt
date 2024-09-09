package com.rubensousa.carioca.report.annotations

// TODO: Enable individual recording option for each test
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ScreenshotConfig(
    val keepOnSuccess: Boolean = false,
)
