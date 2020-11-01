package com.duzhaokun123.arknightshelper.utils

import java.util.*

object ObjectCache {
    private val objectMap: MutableMap<String, Any?> = WeakHashMap()
    fun put(value: Any?): String {
        var key = "" + System.currentTimeMillis()
        if (value != null) {
            key += value.hashCode()
        }
        objectMap[key] = value
        return key
    }

    operator fun get(id: String): Any? {
        return objectMap.remove(id)
    }
}
