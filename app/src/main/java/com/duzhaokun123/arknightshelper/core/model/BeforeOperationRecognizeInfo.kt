package com.duzhaokun123.arknightshelper.core.model

data class BeforeOperationRecognizeInfo(
    var ap: String,
    var consumeAp: Boolean,
    var operation: String,
    var delegated: Boolean,
    var consume: Int
)