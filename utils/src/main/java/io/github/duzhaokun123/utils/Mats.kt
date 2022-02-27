package io.github.duzhaokun123.utils

import android.graphics.Bitmap
import android.graphics.Color
import org.opencv.android.Utils
import org.opencv.core.Mat

fun Mat.surroundWith(width: Int, color: Int = Color.BLACK): Mat {
    return Mat(nMatSurroundWith(this.nativeObj, width, color))
}

external fun nMatSurroundWith(mat: Long, width: Int, color: Int): Long

fun Mat.toBitmap(): Bitmap {
    val re = Bitmap.createBitmap(this.width(), this.height(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(this, re)
    return re
}