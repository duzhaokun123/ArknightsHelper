package com.duzhaokun123.arknightshelper.bases

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.duzhaokun123.arknightshelper.utils.Handler
import com.duzhaokun123.arknightshelper.utils.TipUtil
import kotlin.collections.HashMap


abstract class BaseActivity<layout : ViewDataBinding> : AppCompatActivity(),
    Handler.IHandlerMessageCallback {
    companion object {
        protected const val FULLSCREEN = 0b001
        protected const val NEED_HANDLER = 0b010
    }

    private var config = 0
    private val iRequestPermissionCallbackMap by lazy { HashMap<Int, (IntArray) -> Unit>() }
    private var permissionNum = 0

    protected lateinit var baseBind: layout
        private set

    var handler: Handler? = null
        private set

    var isFirstCreate = true
        private set

    val CLASS_NAME = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            isFirstCreate = false
        }
        super.onCreate(savedInstanceState)

        config = initConfig

        if (config and FULLSCREEN != 0) {
            window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        if (config and NEED_HANDLER != 0) {
            handler = Handler(this)
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        baseBind = DataBindingUtil.setContentView(this, initLayout)

        savedInstanceState?.let { onRestoreInstanceStateSynchronously(it) }
        findViews()
        initView()
        initData()
        TipUtil.registerCoordinatorLayout(this, initRegisterCoordinatorLayout())
    }

    override fun onResume() {
        super.onResume()
        if (config and FULLSCREEN != 0) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TipUtil.unregisterCoordinatorLayout(this)
        handler?.destroy().also {
            handler = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("permissionNum", permissionNum)
    }

    fun requestPermissions(
        permissions: Array<String>,
        requestPermissionCallback: ((IntArray) -> Unit)? = null
    ) {
        requestPermissionCallback?.let { iRequestPermissionCallbackMap[permissionNum] = it }
        requestPermissions(permissions, permissionNum)
        permissionNum++
    }

    fun requestPermissions(
        permission: String,
        requestPermissionCallback: ((IntArray) -> Unit)? = null
    ) {
        requestPermissions(arrayOf(permission), requestPermissionCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val iRequestPermissionCallback = iRequestPermissionCallbackMap[requestCode]
        iRequestPermissionCallback?.invoke(grantResults)
        iRequestPermissionCallbackMap.remove(requestCode)
    }

    abstract val initConfig: Int

    abstract val initLayout: Int

    protected fun onRestoreInstanceStateSynchronously(savedInstanceState: Bundle) {
        permissionNum = savedInstanceState.getInt("permissionNum")
    }

    protected fun findViews() {}
    protected abstract fun initView()
    protected abstract fun initData()

    val startIntent: Intent
        get() = intent

    protected fun initRegisterCoordinatorLayout(): CoordinatorLayout? = null
}
