package com.duzhaokun123.arknightshelper.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.duzhaokun123.arknightshelper.Application
import com.duzhaokun123.arknightshelper.R
import com.duzhaokun123.arknightshelper.bases.BaseOverlayWindow
import com.duzhaokun123.arknightshelper.core.SuUIInteractor
import com.duzhaokun123.arknightshelper.core.arknights.ArknightsHelper
import com.duzhaokun123.arknightshelper.core.logger.CallbackLogger
import com.duzhaokun123.arknightshelper.core.model.ArknightsHelperConfig
import com.duzhaokun123.arknightshelper.databinding.OverlayTest2Binding
import com.duzhaokun123.logtextview.LogTextView
import com.duzhaokun123.overlaywindow.OverlayService.Companion.toAction

class Test2OverlayWindow(context: Context) : BaseOverlayWindow<OverlayTest2Binding>(context) {
    private val arknightsHelper by lazy {
        ArknightsHelper(
            SuUIInteractor,
            CallbackLogger(baseBind.scNoChild.isChecked) { tag, func, msg, level ->
                if (level != Log.DEBUG || baseBind.scDebug.isChecked)
                    Application.runOnUiThread {
                        baseBind.tvCallback.appendLog(
                            LogTextView.parserAndroidLogLevel(level),
                            tag,
                            func,
                            msg
                        )
                    }
            }, ArknightsHelperConfig(canRefill = baseBind.resume.isChecked, refillWithItem = true), this.toAction(), "Test2OverlayWindow"
        )
    }

    private val testSmobj by lazy { ArknightsHelper.OperationOnceState() }

    override val initConfig = 0
    override val initLayout = R.layout.overlay_test2

    override fun initView() {
        baseBind.mini.setOnCheckedChangeListener { buttonView, isChecked ->
            val params = baseBind.root.layoutParams
            if (isChecked) {
                params.height = buttonView.height
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            baseBind.root.layoutParams = params
        }
        baseBind.login.setOnClickListener {
            Thread {
                arknightsHelper.login()
            }.start()
        }
        baseBind.setDelegate.setOnClickListener {
            Thread {
                arknightsHelper.setDelegate()
            }.start()
        }
        baseBind.startOperation.setOnClickListener {
            Thread {
                arknightsHelper.testStartOperation()
            }.start()
        }
        baseBind.onceOperation.setOnClickListener {
            Thread {
                arknightsHelper.operationOnceStatemachine()
            }.start()
        }
        baseBind.onPrepare.setOnClickListener {
            Thread {
                arknightsHelper.onPrepare(testSmobj, config = arknightsHelper.config)
            }.start()
        }
        baseBind.operationLoop.setOnClickListener {
            Thread {
                arknightsHelper.operationLoop()
            }.start()
        }
        baseBind.onTroop.setOnClickListener {
            Thread {
                arknightsHelper.onTroop(testSmobj)
            }.start()
        }
        baseBind.onLevelIpPopup.setOnClickListener {
            Thread {
                arknightsHelper.onLevelUpPopup(testSmobj)
            }.start()
        }
        baseBind.onEndOperation.setOnClickListener {
            Thread {
                arknightsHelper.onEndOperation(testSmobj)
            }.start()
        }
        baseBind.goHome.setOnClickListener {
            Thread {
                arknightsHelper.goHome()
            }.start()
        }
        baseBind.waitForStillImage.setOnClickListener {
            Thread {
                arknightsHelper.waitForStillImage()
            }.start()
        }
        baseBind.quitGame.setOnClickListener {
            Thread {
                arknightsHelper.quitGame()
            }.start()
        }
        baseBind.stop.setOnClickListener {
            selfDestroy()
        }
        baseBind.scNoHead.setOnCheckedChangeListener { _, isChecked ->
            baseBind.tvCallback.simple = isChecked
        }
        baseBind.dailyTask.setOnClickListener {
            Thread {
                arknightsHelper.clearDailyTask()
            }.start()
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