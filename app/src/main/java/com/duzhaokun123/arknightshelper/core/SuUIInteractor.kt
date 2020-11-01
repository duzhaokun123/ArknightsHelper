package com.duzhaokun123.arknightshelper.core

import com.duzhaokun123.arknightshelper.utils.SuUtil
import org.opencv.core.Size

class SuUIInteractor : UIInteractor {
    override val screenSize: Size
        get() = SuUtil.getScreenSize() ?: super.screenSize

    override val canWork: Boolean
        get() = SuUtil.check()

    override fun getScreencap() = SuUtil.getScreencap()

    override fun touch(x: Int, y: Int) {
        SuUtil.inputTap(x, y)
    }

    override fun clickBack() {
        SuUtil.clickBack()
    }

    override fun forceStop(packageName: String) {
        SuUtil.forceStop(packageName)
    }
}