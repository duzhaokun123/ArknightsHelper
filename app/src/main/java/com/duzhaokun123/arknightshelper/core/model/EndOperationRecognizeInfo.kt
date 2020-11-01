package com.duzhaokun123.arknightshelper.core.model

data class EndOperationRecognizeInfo(
    var operation: String,
    var stars: Int,
    var items: MutableSet<Item>,
    var lowConfidence: Boolean
) {
    data class Item(
        var name: String
    )
}