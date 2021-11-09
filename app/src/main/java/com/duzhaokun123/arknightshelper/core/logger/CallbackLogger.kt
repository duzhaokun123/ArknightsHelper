package com.duzhaokun123.arknightshelper.core.logger

import org.opencv.core.Mat

/**
 * @param callback 可能不是ui线程回调
 */
class CallbackLogger(
    private val noChild: Boolean = false, private val noH: Boolean = false,
    private val depth: Int = 0,
    private val callback: (tag: String, func: String, msg: String, level: Int, depth: Int) -> Unit
) : Logger {
    override fun logImg(tag: String, img: Mat, func: String, title: String, level: Int) {
        callback(tag, func, "log img $title", level, depth)
    }

    override fun logH1(tag: String, func: String, msg: String, level: Int) {
        if (noH.not())
            callback(tag, func, "H1 $msg", level, depth)
    }

    override fun logH2(tag: String, func: String, msg: String, level: Int) {
        if (noH.not())
            callback(tag, func, "H2 $msg", level, depth)
    }

    override fun logH3(tag: String, func: String, msg: String, level: Int) {
        if (noH.not())
            callback(tag, func, "H3 $msg", level, depth)
    }

    override fun logText(tag: String, func: String, msg: String, level: Int) {
        callback(tag, func, msg, level, depth)
    }

    override fun logDivider(tag: String, func: String, level: Int) {
        callback(tag, func, "------", level, depth)
    }

    override fun getChild(name: String): Logger {
        return if (noChild) EmptyLogger else CallbackLogger(noChild, noH, depth + 1, callback)
    }

    override fun close() {}
}