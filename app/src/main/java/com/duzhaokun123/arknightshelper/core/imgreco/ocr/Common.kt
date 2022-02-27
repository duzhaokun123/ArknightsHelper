package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.util.Log
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.resize
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.sum
import com.duzhaokun123.arknightshelper.core.logger.Logger
import com.duzhaokun123.arknightshelper.core.model.CommonCheckDialogInfo
import com.duzhaokun123.arknightshelper.core.model.CommonRecognizeDialogInfo
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Size
import kotlin.math.max

object Common {
    private const val TAG = "Common"

    fun checkNavButton(img: Mat, logger: Logger? = null): Boolean {
        val func = "checkNavButton"
        val (_, vh) = Util.getVwvh(img.size())

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
        logger?.logImg(TAG, icon1, func, "icon1", Log.DEBUG)
        logger?.debug(TAG, func, "mse = $mse")
        return mse.sum() < 84 && mse.sum() > 48
    }

    fun checkGetItemPopup(img: Mat, logger: Logger? = null): Boolean {
        val func = "checkGetItemPopup"
        val (vw, vh) = Util.getVwvh(img.size())

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
        logger?.logImg(TAG, icon1, func, "icon1", Log.DEBUG)
        logger?.debug(TAG, func, "mse = $mse")
        return mse.sum() < 84 && mse.sum() > 48
    }

    fun checkSettingScreen(img: Mat, logger: Logger? = null): Boolean {
        val func = "checkSettingScreen"
        val (_, vh) = Util.getVwvh(img.size())

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
        logger?.logImg(TAG, icon1, func, "icon1", Log.DEBUG)
        logger?.debug(TAG, func, "mse = $mse")
        return mse.sum() < 84 && mse.sum() > 48
    }

    fun recognizeDialog(img: Mat): CommonRecognizeDialogInfo? {
        val (type, y) = checkDialog(img) ?: return null
        val (vw, vh) = Util.getVwvh(img.size())
        val content = img.crop(0.0, 22.222 * vh, 100.000 * vw, 64.167 * vh)
        return CommonRecognizeDialogInfo(type, y, PaddleOCR.process(content))
    }

    fun checkDialog(img: Mat): CommonCheckDialogInfo? {
        val oldHeight = img.height()
        val i = img.resize(Size(1280.0, 720.0)).crop(0.0, 360.0, 1280.0, 640.0)
        val yesNo = Resources.loadImage("common/dialog_2btn.png")!!
        val ok = Resources.loadImage("common/dialog_1btn.png")!!
        val (pt1, coef1) = Imgops.matchTemplate(i, yesNo)
        val (pt2, coef2) = Imgops.matchTemplate(i, ok)
        Log.d(TAG, "checkDialog: pt1: $pt1, coef1: $coef1, pt2: $pt2, coef2: $coef2")
        if (max(coef1, coef2) > 0.5) {
            return if (coef1 > coef2)
                CommonCheckDialogInfo(
                    CommonCheckDialogInfo.Type.YES_NO,
                    (pt1.y + 360) / 720 * oldHeight
                )
            else
                CommonCheckDialogInfo(
                    CommonCheckDialogInfo.Type.OK,
                    (pt2.y + 360) / 720 * oldHeight
                )
        }
        return null
    }

    fun getDialogRightButtonRect(img: Mat): Rect {
        val (vw, vh) = Util.getVwvh(img.size())
        val (type, y) = checkDialog(img)!!
        assert(type == CommonCheckDialogInfo.Type.YES_NO)
        return Rect(doubleArrayOf(50 * vw, y + 2 * vh, 100 * vw, y + 10 * vh))
    }

    fun getDialogLeftButtonRect(img: Mat): Rect {
        val (vw, vh) = Util.getVwvh(img.size())
        val (type, y) = checkDialog(img)!!
        assert(type == CommonCheckDialogInfo.Type.YES_NO)
        return Rect(doubleArrayOf(0.0, y + 2 * vh, 50 * vw, y + 10 * vh))
    }

    fun getDialogOkButtonRect(img: Mat): Rect {
        val (vw, vh) = Util.getVwvh(img.size())
        val (type, y) = checkDialog(img)!!
        assert(type == CommonCheckDialogInfo.Type.OK)
        return Rect(doubleArrayOf(25 * vw, y + 2 * vh, 75 * vw, y + 10 * vh))
    }
}
