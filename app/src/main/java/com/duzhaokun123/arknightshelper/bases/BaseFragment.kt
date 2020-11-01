package com.duzhaokun123.arknightshelper.bases

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.duzhaokun123.arknightshelper.utils.Handler

abstract class BaseFragment<layout : ViewDataBinding> : Fragment(),
    Handler.IHandlerMessageCallback {
    companion object {
        protected const val NEED_HANDLER = 0b01
    }
    protected val CLASS_NAME = this.javaClass.simpleName

    protected lateinit var baseBind: layout
        private set

    protected var handler: Handler? = null
        private set

    var isFirstCreate = true
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            isFirstCreate = false
        }
        super.onCreate(savedInstanceState)
        val config = initConfig
        if (config and NEED_HANDLER == NEED_HANDLER) {
            handler = Handler(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseBind = DataBindingUtil.inflate(inflater, initLayout, container, false)
        val parentView = baseBind.root
        savedInstanceState?.let { onRestoreInstanceState(it) }
        findViews(parentView)
        initView()
        initData()
        return parentView
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.destroy().also {
            handler = null
        }
    }

    val baseActivity: BaseActivity<*>?
        get() {
            val activity: Activity? = activity
            return if (activity is BaseActivity<*>) {
                activity
            } else {
                null
            }
        }

    val requireBaseActivity: BaseActivity<*>
        get() = baseActivity
            ?: throw IllegalStateException("Fragment $this not attached to an baseActivity.")

    protected abstract val initConfig: Int
    protected abstract val initLayout: Int
    protected fun onRestoreInstanceState(savedInstanceState: Bundle) {}
    protected fun findViews(parentView: View?) {}
    protected abstract fun initView()
    protected abstract fun initData()
}
