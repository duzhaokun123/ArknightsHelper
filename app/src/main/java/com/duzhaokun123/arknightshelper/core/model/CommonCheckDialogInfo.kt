package com.duzhaokun123.arknightshelper.core.model

data class CommonCheckDialogInfo(
    val type: Type,
    val y: Double
) {
    enum class Type {
        YES_NO, OK
    }
}