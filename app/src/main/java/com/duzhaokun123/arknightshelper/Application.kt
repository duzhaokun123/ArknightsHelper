package com.duzhaokun123.arknightshelper

import android.app.Notification
import android.os.Message
import androidx.appcompat.app.AppCompatDelegate
import com.duzhaokun123.arknightshelper.utils.Handler
import com.duzhaokun123.arknightshelper.utils.NotificationUtil
import com.duzhaokun123.arknightshelper.utils.Settings
import com.duzhaokun123.overlaywindow.OverlayService
//import com.google.gson.Gson
import com.topjohnwu.superuser.Shell
import org.opencv.android.OpenCVLoader

class Application : android.app.Application(), Handler.IHandlerMessageCallback {
    companion object {
        lateinit var instance: Application
            private set

        private lateinit var handler: Handler

        fun runOnUiThread(callback: Runnable?) {
            val message = Message.obtain(handler, callback)
            handler.sendMessage(message)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Settings.init(this)
        AppCompatDelegate.setDefaultNightMode(Settings.Layout.uiMode)

        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )

        NotificationUtil.setContext(this)
        NotificationUtil.init(this)

        val overlayNotificationId = NotificationUtil.getNewId()
        val overlayNotification = Notification.Builder(this, NotificationUtil.CHANNEL_ID_OVERLAY)
            .setSmallIcon(R.drawable.ic_android)
            .setContentTitle(getString(R.string.showing_overlay))
            .build()
        NotificationUtil.register(overlayNotificationId, overlayNotification)
        OverlayService.init(overlayNotificationId, overlayNotification)

        OpenCVLoader.initDebug()
    }

    init {
        instance = this
        handler = Handler(this)
    }
}

val application get() = Application.instance

//val gson by lazy { Gson() }