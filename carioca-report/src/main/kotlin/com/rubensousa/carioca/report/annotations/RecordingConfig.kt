package com.rubensousa.carioca.report.annotations

// TODO: Enable individual recording option for each test
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RecordingConfig(
    val enabled: Boolean = true,
    val keepOnSuccess: Boolean = false,
)
