package com.duzhaokun123.arknightshelper.core.logger

import android.util.Log
import org.opencv.core.Mat
import java.io.Closeable

interface Logger : Closeable {
    fun logImg(
        tag: String,
        img: Mat,
        func: String? = null,
        title: String = "notitle",
        level: Int = Log.INFO
    )

    fun logH1(tag: String, msg: String, level: Int = Log.INFO)

    fun logH2(tag: String, msg: String, level: Int = Log.INFO)

    fun logH3(tag: String, msg: String, level: Int = Log.INFO)

    fun logText(tag: String, msg: String, level: Int = Log.INFO)

    fun logDivider(tag: String, level: Int = Log.INFO)

    fun getChild(name: String): Logger

    fun info(tag: String, msg: String) = logText(tag, msg, Log.INFO)
    fun debug(tag: String, msg: String) = logText(tag, msg, Log.DEBUG)
    fun error(tag: String, msg: String) = logText(tag, msg, Log.ERROR)
    fun warring(tag: String, msg: String) = logText(tag, msg, Log.WARN)
    fun verbose(tag: String, msg: String) = logText(tag, msg, Log.VERBOSE)
    fun assert(tag: String, msg: String) = logText(tag, msg, Log.ASSERT)

}