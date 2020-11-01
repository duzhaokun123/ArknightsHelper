package com.duzhaokun123.arknightshelper.core.logger

import org.opencv.core.Mat

class MultiLogger(private vararg val loggers: Logger) : Logger {
    override fun logImg(tag: String, img: Mat, func: String?, title: String, level: Int) {
        loggers.forEach { it.logImg(tag, img, func, title, level) }
    }

    override fun logH1(tag: String, msg: String, level: Int) {
        loggers.forEach { it.logH1(tag, msg, level) }
    }

    override fun logH2(tag: String, msg: String, level: Int) {
        loggers.forEach { it.logH2(tag, msg, level) }
    }

    override fun logH3(tag: String, msg: String, level: Int) {
        loggers.forEach { it.logH3(tag, msg, level) }
    }

    override fun logText(tag: String, msg: String, level: Int) {
        loggers.forEach { it.logText(tag, msg, level) }
    }

    override fun logDivider(tag: String, level: Int) {
        loggers.forEach { it.logDivider(tag, level) }
    }

    override fun getChild(name: String): Logger {
        val childs = Array(loggers.size) { i -> loggers[i].getChild(name) }
        return MultiLogger(*childs)
    }

    override fun close() {
        loggers.forEach { it.close() }
    }
}