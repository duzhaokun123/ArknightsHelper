package com.duzhaokun123.arknightshelper.utils

fun String?.notEmptyOrNull(): String? {
    return if (this.isNullOrEmpty().not()) {
        this
    } else {
        null
    }
}