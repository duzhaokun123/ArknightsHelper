package com.duzhaokun123.arknightshelper.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.duzhaokun123.arknightshelper.Application
import com.duzhaokun123.arknightshelper.R
import com.duzhaokun123.arknightshelper.bases.BaseOverlayWindow
import com.duzhaokun123.arknightshelper.core.SuUIInteractor
import com.duzhaokun123.arknightshelper.core.arknights.ArknightsHelper
import com.duzhaokun123.arknightshelper.core.logger.CallbackLogger
import com.duzhaokun123.arknightshelper.core.model.ArknightsHelperConfig
import com.duzhaokun123.arknightshelper.databinding.OverlayOperationLoopBinding
import com.duzhaokun123.arknightshelper.utils.ViewUtil.scrollToBottom
import com.duzhaokun123.arknightshelper.utils.times
import com.duzhaokun123.logtextview.LogTextView
import com.duzhaokun123.overlaywindow.OverlayService.Companion.toAction
import kotlinx.coroutines.delay

class OperationLoopOverlayWindow(context: Context) :
    BaseOverlayWindow<OverlayOperationLoopBinding>(context) {
    private val helper by lazy {
        ArknightsHelper(
            SuUIInteractor,
            CallbackLogger(noChild = true, noH = true) { tag, func, msg, level, depth ->
                if (level != Log.DEBUG) {
                    Application.runOnUiThread {
                        baseBind.ltv.append("|" * depth)
                        baseBind.ltv.appendLog(
                            LogTextView.parserAndroidLogLevel(level), tag, func, msg
                        )
                        delay(200)
                        baseBind.sv.scrollToBottom()
                    }
                }
            }, ArknightsHelperConfig(), this.toAction(), "OperationLoop"
        )
    }

    private var workThread: Thread? = null

    override val initConfig = 0
    override val initLayout = R.layout.overlay_operation_loop

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        baseBind.ltv.simple = true

        baseBind.cbFocus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) getFocus() else releaseFocus()
        }
        baseBind.ibClose.setOnClickListener { stopAndClose() }
        baseBind.btnStart.setOnClickListener {
            if (workThread == null) {
                Thread {
                    helper.operationLoop(baseBind.etTimes.text.toString().toInt())
                    if (baseBind.cbQuitWhenFinish.isChecked) {
                        helper.quitGame()
                    }
                    workThread = null
                }.also {
                    workThread = it
                }.start()
            }
        }
//        baseBind.btnStop.setOnClickListener {
//
//        }
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

    private fun stopAndClose() {
        // TODO: 20-11-7 stop
        if (workThread == null) {
            selfDestroy()
        }
    }
}