package com.duzhaokun123.arknightshelper.utils

import android.os.Build
import android.os.FileUtils
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


object IOUtil {
    @Throws(IOException::class)
    fun copy(`in`: InputStream, out: OutputStream) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            FileUtils.copy(`in`, out)
        } else {
            val bytes = ByteArray(50000)
            var len: Int
            while (`in`.read(bytes, 0, bytes.size).also { len = it } != -1) {
                out.write(bytes, 0, len)
            }
        }
    }
}