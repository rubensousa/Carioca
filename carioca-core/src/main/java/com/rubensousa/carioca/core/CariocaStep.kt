package com.rubensousa.carioca.core


fun step(title: String, action: () -> Unit) {

}

data class CariocaStep(
    val title: String,
    val action: () -> Unit
)
