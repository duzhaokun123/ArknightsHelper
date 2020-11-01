package com.duzhaokun123.arknightshelper.utils

import com.topjohnwu.superuser.Shell
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import java.io.File

object SuUtil {
    fun check(): Boolean {
        return Shell.su("true").exec().isSuccess
    }

    fun getScreencap(): Mat {
        val file = File.createTempFile("screencap-", ".png")
        Shell.su("screencap -p ${file.path}").exec()
        val re = Imgcodecs.imread(file.path)
        file.delete()
        return re
    }

    fun inputTap(x: Int, y: Int) {
        Shell.su("input tap $x $y").exec()
    }

    fun getScreenSize(): Size? {
        val result = Shell.su("wm size").exec()
        if (result.isSuccess) {
            try {
                val s = result.out[0] // Physical size: 1080x2160
                val w = s.substring(15, 19).toIntOrNull()
                val h = s.substring(20, 24).toIntOrNull()
                if (h != null && w != null) {
                    return Size(h.toDouble(), w.toDouble())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun clickBack() {
        Shell.su("input keyevent 4").exec()
    }

    fun forceStop(packageName: String) {
        Shell.su("am force-stop $packageName").exec()
    }
}