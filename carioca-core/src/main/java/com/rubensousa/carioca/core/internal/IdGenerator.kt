package com.rubensousa.carioca.core.internal

import java.util.UUID

internal object IdGenerator {

    fun get(): String {
        return UUID.randomUUID().toString()
    }

}
