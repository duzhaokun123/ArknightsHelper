package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.util.Log
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.sum
import com.duzhaokun123.arknightshelper.core.logger.Logger
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

object Common {
    const val TAG = "Common"

    fun checkNavButton(img: Mat, logger: Logger? = null): Boolean {
        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var icon1 = img.crop(
            Rect(
                Point(3.194 * vh, 2.222 * vh),
                Point(49.722 * vh, 7.917 * vh)
            )
        )
        var icon2 = Resources.loadImage("common/navbutton.png")!!

        Imgops.uniformSize(icon1, icon2).let {
            icon1 = it.first
            icon2 = it.second
        }

        val mse = Imgops.compareMse(icon1, icon2)
        logger?.logImg(TAG, icon1, "checkNavButton", "icon1", Log.DEBUG)
        logger?.debug(TAG, "checkNavButton: mse = $mse")
        return mse.sum() < 84 && mse.sum() > 48
    }

    fun checkGetItemPopup(img: Mat, logger: Logger? = null): Boolean {
        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var icon1 = img.crop(
            Rect(
                Point(50 * vw - 6.389 * vh, 5.556 * vh),
                Point(50 * vw + 8.426 * vh, 18.981 * vh)
            )
        )
        var icon2 = Resources.loadImage("common/getitem.png")!!

        Imgops.uniformSize(icon1, icon2).let {
            icon1 = it.first
            icon2 = it.second
        }

        val mse = Imgops.compareMse(icon1, icon2)
        logger?.logImg(TAG, icon1, "checkGetItemPopup", "icon1", Log.DEBUG)
        logger?.debug(TAG, "checkGetItemPopup: mse = $mse")
        return mse.sum() < 84 && mse.sum() > 48
    }

    fun checkSettingScreen(img: Mat, logger: Logger? = null): Boolean {
        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var icon1 = img.crop(
            Rect(
                Point(4.722 * vh, 3.750 * vh),
                Point(19.444 * vh, 8.333 * vh)
            )
        )
        var icon2 = Resources.loadImage("common/settingback.png")!!

        Imgops.uniformSize(icon1, icon2).let {
            icon1 = it.first
            icon2 = it.second
        }

        val mse = Imgops.compareMse(icon1, icon2)
        logger?.logImg(TAG, icon1, "checkSettingScreen", "icon1", Log.DEBUG)
        logger?.debug(TAG, "checkSettingScreen: mse = $mse")
        return mse.sum() < 84 && mse.sum() > 48
    }
}
