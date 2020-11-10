package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.graphics.Bitmap
import com.duzhaokun123.arknightshelper.BuildConfig
import com.duzhaokun123.arknightshelper.application
import com.duzhaokun123.arknightshelper.core.logger.Logger
import com.duzhaokun123.arknightshelper.utils.IOUtil
import com.googlecode.tesseract.android.TessBaseAPI
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.io.FileOutputStream
import java.util.*

object TesseractOCR {
    private const val TAG = "TesseractOCR"

    private val tessBaseAPI by lazy {
        copyAssets()
        TessBaseAPI().apply {
            init(application.cacheDir.path, "chi_sim")
            setDebug(BuildConfig.DEBUG)
        }
    }

    fun process(img: Mat): String? {
        tessBaseAPI.clear()
        val bitmap = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(img, bitmap)
        tessBaseAPI.setImage(bitmap)
        tessBaseAPI
        return tessBaseAPI.utF8Text
    }

    private fun copyAssets() {
        val file = File(application.cacheDir, "tessdata/chi_sim.traineddata")
        if (file.exists()) {
            return
        }
        File(file.parent!!).let { parent ->
            if (parent.exists().not()) {
                parent.mkdirs()
            }
        }
        val inputStream = application.assets.open("tessdata/chi_sim.traineddata")
        FileOutputStream(file).use {
            IOUtil.copy(inputStream, it)
        }
        inputStream.close()
    }

    fun String.fixStageName(logger: Logger? = null): String {
        val s1 = this.replace(Regex("[_—一]"), "-")
            .toUpperCase(Locale.getDefault())
            .replace("\'", "")
        val s2 =
            if (s1.startsWith('0') && s1.startsWith("0-").not()) {
                s1.replaceFirst("0", "O")
            } else if (s1.startsWith("O-")) {
                s1.replaceFirst('O', '0')
            } else {
                s1
            }
        val s3 =
            if (s2.startsWith("R--")) {
                s2.replaceFirst("R--", "RI-")
            } else {
                s2
            }.replace(" ", "")
        logger?.debug(TAG, "String.fixStageName", "$this fix to $s3")
        return s3
    }

    fun String.fixApString(logger: Logger? = null): String {
        val s1 = this.replace(Regex("[oO]"), "0")
        logger?.debug(TAG, "String.fixApString", "$this fix to $s1")
        return s1
    }
}