package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.duzhaokun123.arknightshelper.BuildConfig
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.InputStream

object Util {
    fun getVwvh(width: Double, height: Double): Pair<Double, Double> {
        return Pair(width / 100, height / 100)
    }

    fun getVwvh(size: Size) = getVwvh(size.width, size.height)

    fun Mat.resize(size: Size): Mat {
        val dst = Mat()
        Imgproc.resize(this, dst, size)
        return dst
    }

    fun Mat.crop(
        left: Double,
        upper: Double,
        right: Double,
        lower: Double,
        copy: Boolean = false
    ): Mat = crop(Rect(Point(left, upper), Point(right, lower)), copy)

    fun Mat.crop(rect: Rect, copy: Boolean = false): Mat {
        var re = Mat(this, rect)
        if (copy) {
            re = re.clone()
        }
        return re
    }

    operator fun Mat.minus(other: Mat): Mat {
        val dst = Mat()
        Core.subtract(this, other, dst)
        return dst
    }

    operator fun Mat.times(other: Mat): Mat {
        val dst = Mat()
        Core.multiply(this, other, dst)
        return dst
    }

    fun Scalar.sum() = this.`val`.sum()

    fun loadMatFromFileStream(`in`: InputStream): Mat? {
        val bitmap: Bitmap
        try {
            bitmap = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return null
        }
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        val dst = Mat()
        Imgproc.cvtColor(mat, dst, Imgproc.COLOR_RGBA2BGR)
        return dst
    }

    fun getLogLevelString(level: Int): String {
        return when (level) {
            -1 -> ""
            Log.INFO -> "I"
            Log.DEBUG -> "D"
            Log.ASSERT -> "A"
            Log.ERROR -> "E"
            Log.VERBOSE -> "V"
            Log.WARN -> "W"
            else -> "$level"
        }
    }

    operator fun Rect.times(scale: Int): Rect {
        return Rect(this.x * scale, this.y * scale, this.width * scale, this.height * scale)
    }
}