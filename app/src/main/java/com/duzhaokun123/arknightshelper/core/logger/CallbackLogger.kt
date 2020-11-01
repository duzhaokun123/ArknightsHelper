package com.duzhaokun123.arknightshelper.core.logger

import org.opencv.core.Mat

/**
 * @param callback 可能不是ui线程回调
 */
class CallbackLogger(
    private val noChild: Boolean = false,
    private val callback: (tag: String, msg: String, level: Int) -> Unit
) : Logger {
    override fun logImg(tag: String, img: Mat, func: String?, title: String, level: Int) {
        callback(tag, "$func: log img $title", level)
    }

    override fun logH1(tag: String, msg: String, level: Int) {
        callback(tag, "H1 $msg", level)
    }

    override fun logH2(tag: String, msg: String, level: Int) {
        callback(tag, "H2 $msg", level)
    }

    override fun logH3(tag: String, msg: String, level: Int) {
        callback(tag, "H3 $msg", level)
    }

    override fun logText(tag: String, msg: String, level: Int) {
        callback(tag, msg, level)
    }

    override fun logDivider(tag: String, level: Int) {
        callback(tag, "---", level)
    }

    override fun getChild(name: String): Logger {
        return if (noChild) {
            EmptyLogger
        } else {
            this
        }
    }

    override fun close() {}
}