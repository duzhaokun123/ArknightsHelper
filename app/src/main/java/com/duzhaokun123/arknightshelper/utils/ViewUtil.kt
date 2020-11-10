package com.duzhaokun123.arknightshelper.utils

import android.widget.ScrollView
import androidx.core.view.get

object ViewUtil {
    fun ScrollView.scrollToBottom() {
        smoothScrollTo(0, get(0).height)
    }
}