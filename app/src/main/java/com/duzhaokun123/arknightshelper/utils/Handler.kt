package com.duzhaokun123.arknightshelper.utils

import android.os.Handler
import android.os.Looper
import android.os.Message


class Handler(private var iHandlerMessageCallback: IHandlerMessageCallback?) :
    Handler(Looper.myLooper()!!) {
    override fun handleMessage(msg: Message) {
        if (iHandlerMessageCallback != null) {
            iHandlerMessageCallback!!.handlerCallback(msg)
        }
    }

    fun destroy() {
        removeCallbacksAndMessages(null)
        iHandlerMessageCallback = null
    }

    interface IHandlerMessageCallback {
        fun handlerCallback(msg: Message) {}
    }
}
