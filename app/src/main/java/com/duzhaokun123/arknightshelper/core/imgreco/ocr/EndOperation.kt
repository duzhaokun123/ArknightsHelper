package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.util.Log
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.TesseractOCR.fixStageName
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.logger.Logger
import com.duzhaokun123.arknightshelper.core.model.EndOperationRecognizeInfo
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

object EndOperation {
    private const val TAG = "EndOperation"

    fun recognize(img: Mat, logger: Logger? = null): EndOperationRecognizeInfo? {
        val func = "recognize"
        logger?.logH2(TAG, func)

        val t0 = System.currentTimeMillis()
        val (vw, vh) = Util.getVwvh(img.size())

        val lower = img.crop(
            Rect(
                Point(0.0, 61.111 * vh),
                Point(100 * vw, 100 * vh)
            )
        )
        logger?.logImg(TAG, lower, func, "lower")

        val operationId = lower.crop(
            Rect(
                Point(0.0, 4.444 * vh),
                Point(23.611 * vh, 11.388 * vh)
            )
        )
        logger?.logImg(TAG, operationId, func, "operationId")
        val operationIdStr = (TesseractOCR.process(operationId) ?: return null).fixStageName(logger)
        logger?.logText(TAG, func, "operationIdStr: $operationIdStr")

        val stars = lower.crop(
            Rect(
                Point(23.611 * vh, 6.759 * vh),
                Point(53.241 * vh, 16.944 * vh)
            )
        )
        val starsStatus = tellStars(stars, logger)
        logger?.logText(TAG, func, "starsStatus = $starsStatus")

        val recoresult = EndOperationRecognizeInfo(operationIdStr, starsStatus, HashSet(), false)

        val items = lower.crop(
            Rect(
                Point(68.241 * vh, 10.926 * vh),
                Point(lower.width().toDouble(), 35.000 * vh)
            )
        )
        logger?.logImg(TAG, items, func, "items")

        // TODO: 20-11-1 掉落识别
//        var x = 6.667 * vh
//        var y = 18.519 * vh
//        var linedet = items.crop(Rect(Point(x, y), Point(x + 1, items.height().toDouble())))
//        val a  = findJumping(linedet.reshape(linedet.height()), 64)
        recoresult.lowConfidence = true

        return recoresult
    }

    fun checkLevelUpPopup(img: Mat): Boolean {
        val (vw, vh) = Util.getVwvh(img.size())
        val lui = img.crop(
            Rect(
                Point(50 * vw - 48.796 * vh, 47.685 * vh),
                Point(50 * vw - 23.148 * vh, 56.019 * vh)
            )
        )
        val lut = TesseractOCR.process(lui) ?: return false
        return "提升" in lut
    }

    fun checkEndOperation(img: Mat): Boolean {
        val (_, vh) = Util.getVwvh(img.size())
        val template = Resources.loadImage("end_operation/friendship.png")!!
        val operationEndImg = img.crop(117.083 * vh, 64.306 * vh, 121.528 * vh, 69.583 * vh)
        return Imgops.compareCcoeff(Imgops.uniformSize(template, operationEndImg)) > 0.8
    }

    fun checkEndOperationAlt(img: Mat): Boolean {
        val (_, vh) = Util.getVwvh(img.size())
        val template = Resources.loadImage("end_operation/end.png")!!
        val operationEndImg = img.crop(4.722 * vh, 80.278 * vh, 56.389 * vh, 93.889 * vh)

        return Imgops.compareCcoeff(Imgops.uniformSize(template, operationEndImg)) > 0.7
    }

    /**
     * @return 不是 3 就是 2 , 反正我没见过 1
     */
    private fun tellStars(stars: Mat, logger: Logger? = null): Int {
        val func = "tellStars"
        logger?.logH3(TAG, func, level = Log.DEBUG)
        val img1: Mat
        var img2 = Resources.loadImage("end_operation/stars3.png")!!
        Imgops.uniformSize(stars, img2).let {
            img1 = it.first
            img2 = it.second
        }
        val ccoeff = Imgops.compareCcoeff(img1, img2)
        logger?.logImg(TAG, img1, func, "img1", Log.DEBUG)
        logger?.debug(TAG, func, "cceff = $ccoeff")
        logger?.logDivider(TAG, func, Log.DEBUG)
        return if (ccoeff > 0.9) 3 else 2
    }
}