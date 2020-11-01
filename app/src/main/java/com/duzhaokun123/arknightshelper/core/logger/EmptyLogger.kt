package com.duzhaokun123.arknightshelper.core.logger

import org.opencv.core.Mat

object EmptyLogger : Logger {
    override fun logImg(tag: String, img: Mat, func: String?, title: String, level: Int) {}

    override fun logH1(tag: String, msg: String, level: Int) {}

    override fun logH2(tag: String, msg: String, level: Int) {}

    override fun logH3(tag: String, msg: String, level: Int) {}

    override fun logText(tag: String, msg: String, level: Int) {}

    override fun logDivider(tag: String, level: Int) {}

    override fun getChild(name: String) = this

    override fun close() {}
}