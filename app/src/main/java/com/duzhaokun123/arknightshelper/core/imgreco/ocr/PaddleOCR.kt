package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import com.baidu.paddle.lite.demo.ocr.OcrResultModel
import com.baidu.paddle.lite.demo.ocr.Predictor
import com.duzhaokun123.arknightshelper.application
import io.github.duzhaokun123.utils.toBitmap
import org.opencv.core.Mat

object PaddleOCR {
    private const val TAG = "PaddleOCR"

    private val predictor by lazy { Predictor().apply {
        init(application, "models/ch_ppocr_mobile_v2", "labels/ppocr_keys_v1.txt", 4, "LITE_POWER_HIGH", "BGR", longArrayOf(1, 3, 960), floatArrayOf(0.485F, 0.456F, 0.406F), floatArrayOf(0.229F, 0.224F, 0.225F), 0.1F)
    } }

    fun process(img: Mat, removeEndBreak: Boolean = true): String? {
        return processRow(img)?.toSimpleString()?.let {
            if (removeEndBreak && it.endsWith("\n"))
                it.substring(0, it.length - 1)
            else it.toString()
        }
    }

    @Synchronized
    fun processRow(img: Mat): ArrayList<OcrResultModel>? {
        val bitmap = img.toBitmap()
        predictor.setInputImage(bitmap)
        return if (predictor.runModel()) predictor.resultModels() else null
    }

    private fun ArrayList<OcrResultModel>.toSimpleString(): StringBuilder {
        val sb = StringBuilder()
        this.forEach {
            sb.appendLine(it.label)
        }
        return sb
    }
}


