package com.duzhaokun123.arknightshelper.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.duzhaokun123.arknightshelper.R
import com.duzhaokun123.arknightshelper.bases.BaseActivity
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.*
import com.duzhaokun123.arknightshelper.core.logger.AndroidLogger
import com.duzhaokun123.arknightshelper.core.logger.CallbackLogger
import com.duzhaokun123.arknightshelper.core.logger.MDLogger
import com.duzhaokun123.arknightshelper.core.logger.MultiLogger
import com.duzhaokun123.arknightshelper.core.model.CommonCheckDialogInfo
import com.duzhaokun123.arknightshelper.databinding.ActivityMainBinding
import com.duzhaokun123.arknightshelper.utils.TipUtil
import com.duzhaokun123.arknightshelper.utils.times
import com.duzhaokun123.overlaywindow.OverlayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : BaseActivity<ActivityMainBinding>() {
    companion object {
        const val REQUEST_OPEN_FILE_BEFORE_OPERATION_RECOGNIZE = 1
        const val REQUEST_OPEN_FILE_BEFORE_OPERATION_CCTR = 2
        const val REQUEST_OPEN_FILE_END_OPERATION_RECOGNIZE = 3
        const val REQUEST_OPEN_FILE_DIALOG = 4
        const val REQUEST_OPEN_FILE_OCR = 5
    }

    private val logger by lazy {
        MultiLogger(CallbackLogger { tag, func, msg, level, depth ->
            runOnUiThread {
                baseBind.tvCallback.append("${"|" * depth}${Util.getLogLevelString(level)}/$tag: $func: $msg\n")
            }
        }, MDLogger("Test"), AndroidLogger())
    }

    override val initConfig: Int = 0
    override val initLayout: Int = R.layout.activity_main

    override fun initView() {
        baseBind.overlayTest.setOnClickListener {
            OverlayService.addWindow(TestOverlayWindow(this)).show()
        }
        baseBind.overlayTest2.setOnClickListener {
            OverlayService.addWindow(Test2OverlayWindow(this)).show()
        }
        baseBind.fileBeforeOperationRecognize.setOnClickListener {
            requestImgFor(REQUEST_OPEN_FILE_BEFORE_OPERATION_RECOGNIZE)
        }
        baseBind.fileBeforeOperationCctr.setOnClickListener {
            requestImgFor(REQUEST_OPEN_FILE_BEFORE_OPERATION_CCTR)
        }
        baseBind.fileEndOperationRecognize.setOnClickListener {
            requestImgFor(REQUEST_OPEN_FILE_END_OPERATION_RECOGNIZE)
        }
        baseBind.cleanCache.setOnClickListener {
            externalCacheDir?.deleteRecursively()
        }
        baseBind.gc.setOnClickListener {
            System.gc()
        }
        baseBind.runFinalization.setOnClickListener {
            System.runFinalization()
        }
        baseBind.operationLoop.setOnClickListener {
            OverlayService.addWindow(OperationLoopOverlayWindow(this)).show()
        }
        baseBind.fileDialog.setOnClickListener {
            requestImgFor(REQUEST_OPEN_FILE_DIALOG)
        }
        baseBind.fileOcr.setOnClickListener {
            requestImgFor(REQUEST_OPEN_FILE_OCR)
        }
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            contentResolver.openInputStream(data!!.data!!)!!.use {
                Util.loadMatFromFileStream(it)?.let {
                    Thread {
                        val result =
                            when (requestCode) {
                                REQUEST_OPEN_FILE_BEFORE_OPERATION_RECOGNIZE ->
                                    BeforeOperation.recognize(it, logger)
                                REQUEST_OPEN_FILE_BEFORE_OPERATION_CCTR ->
                                    BeforeOperation.checkConfirmTroopRect(it, logger)
                                REQUEST_OPEN_FILE_END_OPERATION_RECOGNIZE ->
                                    EndOperation.recognize(it, logger)
                                REQUEST_OPEN_FILE_DIALOG -> {
                                    val dialog = Common.recognizeDialog(it)
                                    "dialog: $dialog\n" +
                                            if (dialog?.type == CommonCheckDialogInfo.Type.YES_NO) {
                                                "left: ${Common.getDialogLeftButtonRect(it)}\n" +
                                                        "right ${Common.getDialogRightButtonRect(it)}"
                                            } else if (dialog?.type == CommonCheckDialogInfo.Type.OK) {
                                                "ok: ${Common.getDialogOkButtonRect(it)}"
                                            } else ""
                                }
                                REQUEST_OPEN_FILE_OCR ->
                                    PaddleOCR.process(it)
                                else -> "bad code $requestCode"
                            }
                        runOnUiThread {
                            TipUtil.showToast("$result")
                            baseBind.tvCallback.append("$result\n")
                        }
                    }.start()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestImgFor(requestCode: Int) {
        startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }, requestCode)
    }
}