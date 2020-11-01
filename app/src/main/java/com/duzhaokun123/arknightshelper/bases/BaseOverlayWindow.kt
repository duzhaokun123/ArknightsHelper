package com.duzhaokun123.arknightshelper.bases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.duzhaokun123.arknightshelper.utils.Handler
import com.duzhaokun123.overlaywindow.OverlayWindow

abstract class BaseOverlayWindow<layout : ViewDataBinding>(context: Context) :
    OverlayWindow(context),
    Handler.IHandlerMessageCallback {
    companion object {
        protected const val NEED_HANDLER = 0b01
    }

    protected val CLASS_NAME = this.javaClass.simpleName

    protected lateinit var baseBind: layout
        private set

    protected var handler: Handler? = null
        private set

    override fun onCreate() {
        super.onCreate()
        val config = initConfig
        if (config and NEED_HANDLER == NEED_HANDLER) {
            handler = Handler(this)
        }
        baseBind = DataBindingUtil.inflate(LayoutInflater.from(context), initLayout, root, true)
        findViews(baseBind.root)
        initView()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.destroy().also {
            handler = null
        }
    }

    protected abstract val initConfig: Int
    protected abstract val initLayout: Int
    protected fun findViews(parentView: View) {}
    protected abstract fun initView()
    protected abstract fun initData()
}