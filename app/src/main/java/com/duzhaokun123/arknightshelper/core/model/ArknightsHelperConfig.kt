package com.duzhaokun123.arknightshelper.core.model

data class ArknightsHelperConfig(
    val canRefill: Boolean = false,
    val refillWithItem: Boolean = false,
    val refillWithOriginium: Boolean = false,
    val behavior: Behavior = Behavior()
) {
    data class Behavior(
        val skipMistakenDelegation: Boolean = true,
        val allowMistakenDelegation: Boolean = false
    )
}