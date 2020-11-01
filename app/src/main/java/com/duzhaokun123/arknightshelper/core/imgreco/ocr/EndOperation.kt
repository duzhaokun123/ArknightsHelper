package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.util.Log
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.TesseractOCR.fixStageName
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.logger.Logger
import com.duzhaokun123.arknightshelper.core.model.EndOperationRecognizeInfo
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

object EndOperation {
    const val TAG = "EndOperation"

    fun recognize(img: Mat, logger: Logger? = null): EndOperationRecognizeInfo? {
        logger?.logH2(TAG, "recognize")

        val t0 = System.currentTimeMillis()
        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        val lower = img.crop(
            Rect(
                Point(0.0, 61.111 * vh),
                Point(100 * vw, 100 * vh)
            )
        )
        logger?.logImg(TAG, lower, "recognize", "lower")

        val operationId = lower.crop(
            Rect(
                Point(0.0, 4.444 * vh),
                Point(23.611 * vh, 11.388 * vh)
            )
        )
        logger?.logImg(TAG, operationId, "recognize", "operationId")
        val operationIdStr = (TesseractOCR.process(operationId) ?: return null).fixStageName(logger)
        logger?.logText(TAG, "recognize: operationIdStr: $operationIdStr")

        val stars = lower.crop(
            Rect(
                Point(23.611 * vh, 6.759 * vh),
                Point(53.241 * vh, 16.944 * vh)
            )
        )
        logger?.logImg(TAG, stars, "recognize", "stars")
        val starsStatus = tellStars(stars, logger)
        logger?.logText(TAG, "recognize: starsStatus = $starsStatus")

        val recoresult = EndOperationRecognizeInfo(operationIdStr, starsStatus, HashSet(), false)

        val items = lower.crop(
            Rect(
                Point(68.241 * vh, 10.926 * vh),
                Point(lower.width().toDouble(), 35.000 * vh)
            )
        )
        logger?.logImg(TAG, items, "recognize", "items")

        // TODO: 20-11-1 掉落识别
//        var x = 6.667 * vh
//        var y = 18.519 * vh
//        var linedet = items.crop(Rect(Point(x, y), Point(x + 1, items.height().toDouble())))
//        val a  = findJumping(linedet.reshape(linedet.height()), 64)
        recoresult.lowConfidence = true

        return recoresult
    }

    /**
     * @return 不是 3 就是 2 , 反正我没见过 1
     */
    private fun tellStars(stars: Mat, logger: Logger? = null): Int {
        logger?.logH3(TAG, "tellStars", Log.DEBUG)
        val img1: Mat
        var img2 = Resources.loadImage("end_operation/stars3.png")!!
        Imgops.uniformSize(stars, img2).let {
            img1 = it.first
            img2 = it.second
        }
        val ccoeff = Imgops.compareCcoeff(img1, img2)
        logger?.logImg(TAG, img1, "tellStars", "img1", Log.DEBUG)
        logger?.debug(TAG, "tellStars: cceff = $ccoeff")
        logger?.logDivider(TAG, Log.DEBUG)
        return if (ccoeff > 0.9) 3 else 2
    }


}