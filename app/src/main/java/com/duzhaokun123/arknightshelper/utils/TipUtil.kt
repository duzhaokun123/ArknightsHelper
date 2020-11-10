package com.duzhaokun123.arknightshelper.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.duzhaokun123.arknightshelper.application
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*


object TipUtil {
    private val map: MutableMap<Context, CoordinatorLayout> = HashMap()
    fun registerCoordinatorLayout(context: Context?, coordinatorLayout: CoordinatorLayout?) {
        if (context != null && coordinatorLayout != null) {
            map[context] = coordinatorLayout
        }
    }

    fun unregisterCoordinatorLayout(context: Context) {
        map.remove(context)
    }

    fun showToast(msg: CharSequence) {
        Toast.makeText(application, msg, Toast.LENGTH_LONG).show()
    }

    fun showToast(@StringRes resId: Int) {
        Toast.makeText(application, resId, Toast.LENGTH_LONG).show()
    }

    fun showSnackbar(coordinatorLayout: CoordinatorLayout, msg: CharSequence) {
        Snackbar.make(coordinatorLayout, msg, BaseTransientBottomBar.LENGTH_LONG).show()
    }

    fun showSnackbar(coordinatorLayout: CoordinatorLayout, @StringRes resId: Int) {
        showSnackbar(coordinatorLayout, application.getString(resId))
    }

    fun showTip(context: Context?, msg: CharSequence) {
        for (registeredContext in map.keys) {
            if (registeredContext === context) {
                if (context is Activity && context.window.decorView.visibility == View.VISIBLE) {
                    val coordinatorLayout = map[registeredContext]
                    showSnackbar(coordinatorLayout!!, msg)
                } else {
                    showToast(msg)
                }
                return
            }
        }
        showToast(msg)
    }

    fun showTip(context: Context?, @StringRes resId: Int) {
        showTip(context, application.getString(resId))
    }
}