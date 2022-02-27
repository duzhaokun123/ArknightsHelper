package io.github.duzhaokun123.utils

object UtilsLibLoader {
    var loaded = false
        private set

    fun load() {
        if (loaded) return
        System.loadLibrary("utils")
        loaded = true
    }
}