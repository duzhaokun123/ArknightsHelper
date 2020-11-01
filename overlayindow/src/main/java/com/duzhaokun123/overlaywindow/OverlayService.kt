package com.duzhaokun123.overlaywindow

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.properties.Delegates
import kotlin.random.Random

class OverlayService : Service() {
    companion object {
        private const val ACTION_SHOW_WINDOW =
            "com.duzhaokun123.overlaywindow.action.ACTION_SHOW_WINDOW"
        private const val ACTION_DESTROY_WINDOW =
            "com.duzhaokun123.overlaywindow.action.ACTION_DESTROY_WINDOW"
        private const val ACTION_STOP =
            "com.duzhaokun123.overlaywindow.action.ACTION_STOP"

        private const val EXTRA_TAG = "com.duzhaokun123.overlaywindow.extra.TAG"

        private val windowSet by lazy { HashSet<OverlayWindow>() }

        lateinit var notification: Notification
            private set

        var notificationId by Delegates.notNull<Int>()
            private set

        fun init(notificationId: Int, notification: Notification) {
            this.notificationId = notificationId
            this.notification = notification
        }

        fun addWindow(overlayWindow: OverlayWindow, tag: String? = null): WindowAction {
            overlayWindow.tag = tag ?: "${System.currentTimeMillis()}${Random.nextInt()}"
            windowSet.add(overlayWindow)
            return WindowAction(overlayWindow)
        }

        fun findWindowByTag(tag: String): OverlayWindow? {
            windowSet.forEach {
                if (it.tag == tag) {
                    return it
                }
            }
            return null
        }

        fun canDrawOverlays(context: Context): Boolean {
            return Settings.canDrawOverlays(context)
        }

        fun stop(context: Context) {
            context.startService(Intent(context, OverlayService::class.java).apply {
                action = ACTION_STOP
            })
        }

        fun OverlayWindow.toAction() = WindowAction(this)
    }

    private lateinit var windowManager: WindowManager

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(notificationId, notification)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW_WINDOW -> showWindow(it.getStringExtra(EXTRA_TAG))
                ACTION_DESTROY_WINDOW -> destroyWindow(it.getStringExtra(EXTRA_TAG))
                ACTION_STOP -> stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowSet.removeAll {
            windowManager.removeView(it.root)
            true
        }
    }

    private fun showWindow(tag: String?) {
        tag?.let { findWindowByTag(it) }?.let {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.START or Gravity.TOP
                x = 0
                y = 0
            }
            it.root.let { layout ->
                layout.setOnTouchListener(MoveOnTouchListener(it::onIsMovingChanged))
                windowManager.addView(layout, params)
            }
        }
    }

    private fun destroyWindow(tag: String?) {
        tag?.let { findWindowByTag(it) }?.let {
            windowManager.removeView(it.root)
            windowSet.remove(it)
        }
        if (windowSet.isEmpty()) {
            stop()
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    inner class MoveOnTouchListener(private val isMovingCallback: (isMoving: Boolean) -> Unit) :
        View.OnTouchListener {
        private var originalXPos = 0
        private var originalYPos = 0

        private var offsetX = 0f
        private var offsetY = 0f

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isMovingCallback(true)

                    val x = event.rawX
                    val y = event.rawY

                    val location = IntArray(2)
                    v.getLocationOnScreen(location)

                    originalXPos = location[0]
                    originalYPos = location[1]

                    offsetX = x - originalXPos
                    offsetY = y - originalYPos
                }
                MotionEvent.ACTION_MOVE -> {
                    val onScreen = IntArray(2)
                    v.getLocationOnScreen(onScreen)

                    val x = event.rawX
                    val y = event.rawY

                    val params: WindowManager.LayoutParams =
                        v.layoutParams as WindowManager.LayoutParams

                    val newX = (x - offsetX).toInt()
                    val newY = (y - offsetY).toInt()

                    if (newX == originalXPos && newY == originalYPos) {
                        return true
                    }

                    params.x = newX
                    params.y = newY

                    windowManager.updateViewLayout(v, params)
                }
                MotionEvent.ACTION_UP -> {
                    isMovingCallback(false)
                }
            }
            return true
        }
    }

    class WindowAction(val window: OverlayWindow) {
        fun show() {
            if (window.isCreated.not()) {
                window.onCreate()
                window.context.startService(
                    Intent(
                        window.context,
                        OverlayService::class.java
                    ).apply {
                        action = ACTION_SHOW_WINDOW
                        putExtra(EXTRA_TAG, window.tag)
                    })
            }
            if (window.isShowing.not()) {
                window.onShow()
            }
        }

        fun hide() {
            if (window.isShowing) {
                window.onHide()
            }
        }

        fun destroy() {
            if (window.isDestroyed.not()) {
                window.context.startService(
                    Intent(
                        window.context,
                        OverlayService::class.java
                    ).apply {
                        action = ACTION_DESTROY_WINDOW
                        putExtra(EXTRA_TAG, window.tag)
                    })
                window.onDestroy()
            }
        }
    }
}