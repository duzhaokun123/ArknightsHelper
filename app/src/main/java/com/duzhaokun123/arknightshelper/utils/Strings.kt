package com.duzhaokun123.arknightshelper.utils

fun String?.notEmptyOrNull(): String? {
    return if (this.isNullOrEmpty().not()) {
        this
    } else {
        null
    }
}

operator fun String.times(times: Int): String {
    val builder = StringBuilder()
    (0..times).forEach { _ ->
        builder.append(this)
    }
    return builder.toString()
}