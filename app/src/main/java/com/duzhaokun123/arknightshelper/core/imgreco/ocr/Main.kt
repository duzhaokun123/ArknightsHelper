package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.util.Log
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.logger.Logger
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

object Main {
    const val TAG = "Main"

    fun checkMain(img: Mat, logger: Logger? = null): Boolean {
        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var gear1 = img.crop(
            Rect(
                Point(3.148 * vh, 2.037 * vh),
                Point(9.907 * vh, 8.796 * vh)
            )
        )
        var gear2 = Resources.loadImage("main/gear.png")!!
        Imgops.uniformSize(gear1, gear2).let {
            gear1 = it.first
            gear2 = it.second
        }
        val result = Imgops.compareCcoeff(gear1, gear2)
        logger?.logImg(TAG, gear1, "checkMain", "gear1", Log.DEBUG)
        logger?.debug(TAG, "checkMain: ccoeff = $result")
        return result > 0.9
    }
}