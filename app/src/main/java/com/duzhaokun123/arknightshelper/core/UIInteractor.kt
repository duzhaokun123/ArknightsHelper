package com.duzhaokun123.arknightshelper.core

import org.opencv.core.Mat
import org.opencv.core.Size

interface UIInteractor {
    val canWork: Boolean

    val screenSize: Size
        get() = getScreencap().size()

    fun getScreencap(): Mat

    fun touch(x: Double, y: Double)

    fun clickBack()

    fun forceStop(packageName: String)
}