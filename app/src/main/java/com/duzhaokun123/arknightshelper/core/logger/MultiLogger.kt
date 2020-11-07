package com.duzhaokun123.arknightshelper.core.logger

import org.opencv.core.Mat

class MultiLogger(private vararg val loggers: Logger) : Logger {
    override fun logImg(tag: String, img: Mat, func: String, title: String, level: Int) {
        loggers.forEach { it.logImg(tag, img, func, title, level) }
    }

    override fun logH1(tag: String, func: String, msg: String, level: Int) {
        loggers.forEach { it.logH1(tag, func, msg, level) }
    }

    override fun logH2(tag: String, func: String, msg: String, level: Int) {
        loggers.forEach { it.logH2(tag, func, msg, level) }
    }

    override fun logH3(tag: String, func: String, msg: String, level: Int) {
        loggers.forEach { it.logH3(tag, func, msg, level) }
    }

    override fun logText(tag: String, func: String, msg: String, level: Int) {
        loggers.forEach { it.logText(tag, func, msg, level) }
    }

    override fun logDivider(tag: String, func: String, level: Int) {
        loggers.forEach { it.logDivider(tag, func, level) }
    }

    override fun getChild(name: String): Logger {
        val childs = Array(loggers.size) { i -> loggers[i].getChild(name) }
        return MultiLogger(*childs)
    }

    override fun close() {
        loggers.forEach { it.close() }
    }
}