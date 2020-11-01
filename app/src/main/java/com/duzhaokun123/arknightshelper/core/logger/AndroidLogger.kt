package com.duzhaokun123.arknightshelper.core.logger

import android.util.Log
import org.opencv.core.Mat

class AndroidLogger : Logger {
    override fun logImg(tag: String, img: Mat, func: String?, title: String, level: Int) {
        Log.println(level, tag, "$func log img $title")
    }

    override fun logH1(tag: String, msg: String, level: Int) {
        Log.println(level, tag, "h1 $msg")
    }

    override fun logH2(tag: String, msg: String, level: Int) {
        Log.println(level, tag, "H2 $msg")
    }

    override fun logH3(tag: String, msg: String, level: Int) {
        Log.println(level, tag, "H3 $msg")
    }

    override fun logText(tag: String, msg: String, level: Int) {
        Log.println(level, tag, msg)
    }

    override fun logDivider(tag: String, level: Int) {
        Log.println(level, tag, "---")
    }

    override fun getChild(name: String): Logger {
        return this
    }

    override fun close() {}
}