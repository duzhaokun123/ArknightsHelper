package com.duzhaokun123.arknightshelper.core.model

data class ArknightsHelperState(
    var operationStart: Long = 0,
    var refillCount: Int = 0,
    var operationTime: MutableList<Long> = mutableListOf()
)