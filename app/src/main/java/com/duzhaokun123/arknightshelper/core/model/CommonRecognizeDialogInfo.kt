package com.duzhaokun123.arknightshelper.core.model

data class CommonRecognizeDialogInfo(
    val type: CommonCheckDialogInfo.Type,
    val y : Double,
    val ocrResult: String?
)