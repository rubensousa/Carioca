package com.rubensousa.carioca.core.internal

import android.net.Uri
import org.junit.runner.Description

internal data class Test(
    val name: String,
    val className: String,
    val outputDir: Uri,
) {

    companion object {



        fun from(description: Description): Test {
            return Test(
                name = description.methodName,
                className = description.className,
                outputDir = TestOutputLocation.getOutputPath(description)
            )
        }

    }

}