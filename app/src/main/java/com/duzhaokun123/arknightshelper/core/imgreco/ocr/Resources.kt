package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.util.Log
import com.duzhaokun123.arknightshelper.application
import org.opencv.core.Mat
import java.util.*

object Resources {
    const val TAG = "Resources"

    private val imageCache by lazy { WeakHashMap<String, Mat>() }

    fun loadAssetImage(name: String): Mat? {
        application.assets.open("imgreco/$name").use {
            return Util.loadMatFromFileStream(it)
        }
    }

    fun loadImage(name: String): Mat? {
        Log.d(TAG, "loadImage: load $name")

        var re: Mat?

        re = imageCache[name]
        if (re != null) {
            Log.d(TAG, "loadImage: found in imageCache")
        }

        if (re == null) {
            re = loadAssetImage(name)
            if (re != null) {
                Log.d(TAG, "loadImage: found in asset")
            }
        }

        if (re == null) {
            Log.d(TAG, "loadImage: not found")
        } else {
            imageCache[name] = re
        }

        return re
    }
}