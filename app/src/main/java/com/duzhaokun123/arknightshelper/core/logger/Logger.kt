package com.duzhaokun123.arknightshelper.core.logger

import android.util.Log
import org.opencv.core.Mat
import java.io.Closeable

interface Logger : Closeable {
    fun logImg(
        tag: String,
        img: Mat,
        func: String,
        title: String = "notitle",
        level: Int = Log.INFO
    )

    fun logH1(tag: String, func: String, msg: String = func, level: Int = Log.INFO)

    fun logH2(tag: String, func: String, msg: String = func, level: Int = Log.INFO)

    fun logH3(tag: String, func: String, msg: String = func, level: Int = Log.INFO)

    fun logText(tag: String, func: String, msg: String, level: Int = Log.INFO)

    fun logDivider(tag: String, func: String, level: Int = Log.INFO)

    fun getChild(name: String): Logger

    fun info(tag: String, func: String, msg: String) = logText(tag, func, msg, Log.INFO)
    fun debug(tag: String, func: String, msg: String) = logText(tag, func, msg, Log.DEBUG)
    fun error(tag: String, func: String, msg: String) = logText(tag, func, msg, Log.ERROR)
    fun warring(tag: String, func: String, msg: String) = logText(tag, func, msg, Log.WARN)
    fun verbose(tag: String, func: String, msg: String) = logText(tag, func, msg, Log.VERBOSE)
    fun assert(tag: String, func: String, msg: String) = logText(tag, func, msg, Log.ASSERT)

}