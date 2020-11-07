package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import androidx.core.text.isDigitsOnly
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.TesseractOCR.fixStageName
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.crop
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.sum
import com.duzhaokun123.arknightshelper.core.logger.Logger
import com.duzhaokun123.arknightshelper.core.model.BeforeOperationRecognizeInfo
import com.duzhaokun123.arknightshelper.utils.notEmptyOrNull
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import kotlin.math.max

object BeforeOperation {
    private const val TAG = "BeforeOperation"

    fun recognize(img: Mat, logger: Logger? = null): BeforeOperationRecognizeInfo? {
        val func = "recognize"
        logger?.logH2(TAG, func)

        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var apicoin1 = img.crop(
            Rect(
                Point(100 * vw - 29.722 * vh, 2.130 * vh),
                Point(100 * vw - 22.593 * vh, 8.519 * vh)
            )
        )
        var apicoin2 = Resources.loadImage("before_operation/ap_icon.png")!!

        Imgops.uniformSize(apicoin1, apicoin2).let {
            apicoin1 = it.first
            apicoin2 = it.second
        }
        var mse = Imgops.compareMse(apicoin1, apicoin2)
        logger?.logImg(TAG, apicoin1, "recognize", "apicoin1")
        logger?.logText(TAG, func, " mse = $mse")
        val consumeAp =
            mse.sum() < 3251 // FIXME: 20-10-24 这等效于 Python mse = imgops.compare_mse(apicon1, apicon2) 吗
        logger?.logText(TAG, func, " consumeAp: $consumeAp")
        logger?.logDivider(TAG, func)

        val apimg = img.crop(
            Rect(
                Point(100 * vw - 21.019 * vh, 2.917 * vh),
                Point(100 * vw, 8.194 * vh)
            )
        )
        logger?.logImg(TAG, apimg, func, "apimg")
        val apText = TesseractOCR.process(apimg) ?: return null
        logger?.logText(TAG, func, " apText: $apText")
        logger?.logDivider(TAG, func)

        val opidimg = img.crop(
            Rect(
                Point(100 * vw - 55.694 * vh, 11.667 * vh),
                Point(100 * vw - 44.028 * vh, 15.139 * vh)
            )
        )
        logger?.logImg(TAG, opidimg, func, "opidimg")
        val opidtext = (TesseractOCR.process(opidimg) ?: return null).fixStageName(logger)
        logger?.logText(TAG, func, " opidtext: $opidtext")
        logger?.logDivider(TAG, func)

        val delegateimg = img.crop(
            Rect(
                Point(100 * vw - 32.778 * vh, 79.444 * vh),
                Point(100 * vw - 4.861 * vh, 85.417 * vh)
            )
        )
        logger?.logImg(TAG, delegateimg, "recognize", "delegateimg")
        val template = Resources.loadImage("before_operation/delegation_checked.png")!!
        mse = Imgops.compareMse(delegateimg, template)
        logger?.logText(TAG, func, " mse: $mse")
        val delegated = mse.sum() > 100
        logger?.logText(TAG, func, " delegated: $delegated")
        logger?.logDivider(TAG, func)

        val consumeimg = img.crop(
            Rect(
                Point(100 * vw - 12.870 * vh, 94.028 * vh),
                Point(100 * vw - 7.222 * vh, 97.361 * vh)
            )
        )
        logger?.logImg(TAG, consumeimg, "recognize", "consumeimg")
        val rawConsumetext =
            (TesseractOCR.process(consumeimg).notEmptyOrNull() ?: return null).replace('o', '0')
        logger?.logText(TAG, func, " rawConsumetext: $rawConsumetext")
        val consumetext =
            if (rawConsumetext.startsWith("-")) {
                rawConsumetext.substring(1, rawConsumetext.length)
            } else {
                rawConsumetext
            }
        logger?.logText(TAG, func, " consumetext: $consumetext")
        logger?.logDivider(TAG, func)

        if (consumetext.isDigitsOnly().not()) {
            return null
        }

        return BeforeOperationRecognizeInfo(
            apText,
            consumeAp,
            opidtext,
            delegated,
            consumetext.toInt()
        )
    }

    fun checkConfirmTroopRect(img: Mat, logger: Logger? = null): Boolean {
        val func = "checkConfirmTroopRect"
        logger?.logH2(TAG, func)

        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var icon1 = img.crop(
            Rect(
                Point(50 * vw + 57.083 * vh, 64.722 * vh),
                Point(50 * vw + 71.389 * vh, 79.167 * vh)
            )
        )
        var icon2 = Resources.loadImage("before_operation/operation_start.png")!!
        Imgops.uniformSize(icon1, icon2).let {
            icon1 = it.first
            icon2 = it.second
        }
        val ccoeff = Imgops.compareCcoeff(icon1, icon2)
        logger?.logImg(TAG, icon1, "checkConfirmTroopRect", "icon1")
        logger?.logText(TAG, func, "ccoeff = $ccoeff")

        return ccoeff > 0.9
    }

    const val REFILL_TYPE_NONE = 0
    const val REFILL_TYPE_ITEM = 1
    const val REFILL_TYPE_ORIGINIUM = 2

    fun checkApRefillType(img: Mat, logger: Logger? = null): Int {
        val func = "checkApRefillType"
        logger?.logH2(TAG, func)

        val vw: Double
        val vh: Double
        Util.getVwvh(img.size()).let {
            vw = it.first
            vh = it.second
        }

        var icon1 = img.crop(
            Rect(
                Point(50 * vw - 3.241 * vh, 11.481 * vh),
                Point(50 * vw + 42.685 * vh, 17.130 * vh)
            )
        )
        var icon2 = Resources.loadImage("before_operation/refill_with_item.png")!!
        Imgops.uniformSize(icon1, icon2).let {
            icon1 = it.first
            icon2 = it.second
        }
        val mse1 = Imgops.compareMse(icon1, icon2)

        var icon3 = Resources.loadImage("before_operation/refill_with_originium.png")!!
        Imgops.uniformSize(icon1, icon3).let {
            icon1 = it.first
            icon3 = it.second
        }
        val mse2 = Imgops.compareMse(icon1, icon3)

        logger?.logImg(TAG, icon1, "checkApRefillType", "icon1")
        logger?.logText(TAG, func, "mse1 = $mse1, mse2 = $mse2")

        if (max(mse1.sum(), mse2.sum()) < 100) { // FIXME: 20-10-31  都匹配不上
            return REFILL_TYPE_NONE
        }
        if (mse1.sum() > mse2.sum()) {
            return REFILL_TYPE_ITEM
        }

        // FIXME: 20-10-31 始终假设源石足够, 因为开发者的源石实在太多了
        return REFILL_TYPE_ORIGINIUM
    }
}