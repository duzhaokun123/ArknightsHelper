package com.duzhaokun123.arknightshelper.core.logger

import android.util.Log
import org.opencv.core.Mat

class AndroidLogger : Logger {
    override fun logImg(tag: String, img: Mat, func: String, title: String, level: Int) {
        Log.println(level, tag, "$func: log img $title")
    }

    override fun logH1(tag: String,func: String, msg: String, level: Int) {
        Log.println(level, tag, "$func: H1 $msg")
    }

    override fun logH2(tag: String,func: String, msg: String, level: Int) {
        Log.println(level, tag, "$func: H2 $msg")
    }

    override fun logH3(tag: String, func: String,msg: String, level: Int) {
        Log.println(level, tag, "$func: H3 $msg")
    }

    override fun logText(tag: String,func: String, msg: String, level: Int) {
        Log.println(level, tag, "$func: $msg")
    }

    override fun logDivider(tag: String,func: String, level: Int) {
        Log.println(level, tag, "$func: ------")
    }

    override fun getChild(name: String): Logger {
        return this
    }

    override fun close() {}
}