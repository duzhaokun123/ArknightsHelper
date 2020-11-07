package com.duzhaokun123.arknightshelper.ui

import android.animation.ObjectAnimator
import android.content.Context
import com.duzhaokun123.arknightshelper.Application
import com.duzhaokun123.arknightshelper.R
import com.duzhaokun123.arknightshelper.bases.BaseOverlayWindow
import com.duzhaokun123.arknightshelper.core.SuUIInteractor
import com.duzhaokun123.arknightshelper.core.arknights.ArknightsHelper
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util
import com.duzhaokun123.arknightshelper.core.logger.CallbackLogger
import com.duzhaokun123.arknightshelper.core.model.ArknightsHelperConfig
import com.duzhaokun123.arknightshelper.databinding.OverlayTestBinding
import com.duzhaokun123.overlaywindow.OverlayService.Companion.toAction

class TestOverlayWindow(context: Context) : BaseOverlayWindow<OverlayTestBinding>(context) {
    private val arknightsHelper by lazy {
        ArknightsHelper(
            SuUIInteractor(),
            CallbackLogger { tag, func, msg, level ->
                Application.runOnUiThread {
                    baseBind.tvCallback.append("${Util.getLogLevelString(level)}/$tag: $func: $msg\n")
                }
            }, ArknightsHelperConfig(), this.toAction(), "TestOverlayWindow"
        )
    }

    override val initConfig = 0
    override val initLayout = R.layout.overlay_test

    override fun initView() {
        baseBind.testBeforeOperationRecognize.setOnClickListener {
            Thread {
                arknightsHelper.testBeforeOperationRecognize()
            }.start()
        }
        baseBind.testBeforeOperationCctr.setOnClickListener {
            Thread {
                arknightsHelper.testBeforeOperationCCTR()
            }.start()
        }
        baseBind.testEndOperationRecognize.setOnClickListener {
            Thread {
                arknightsHelper.testEndOperationRecognize()
            }.start()
        }
        baseBind.stop.setOnClickListener {
            selfDestroy()
        }
    }

    override fun initData() {

    }

    override fun onIsMovingChanged(isMoving: Boolean) {
        super.onIsMovingChanged(isMoving)
        if (isMoving) {
            ObjectAnimator.ofFloat(baseBind.root, "alpha", 1.0f, 0.7f)
        } else {
            ObjectAnimator.ofFloat(baseBind.root, "alpha", 0.7f, 1.0f)
        }.apply {
            duration = 500
            start()
        }
    }
}