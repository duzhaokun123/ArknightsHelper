package com.duzhaokun123.logtextview

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

import android.util.AttributeSet
import android.util.Log

class LogTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "LogTextView"

        fun parserAndroidLogLevel(level: Int): LogLevel {
            return when (level) {
                Log.ASSERT -> LogLevel.ASSERT
                Log.DEBUG -> LogLevel.DEBUG
                Log.ERROR -> LogLevel.ERROR
                Log.INFO -> LogLevel.INFO
                Log.VERBOSE -> LogLevel.VERBOSE
                Log.WARN -> LogLevel.WARRING
                else -> {
                    Log.e(TAG, "parserAndroidLogLevel: not a level $level")
                    LogLevel.INFO
                }
            }
        }
    }

    enum class LogLevel {
        ASSERT, DEBUG, ERROR, INFO, VERBOSE, WARRING
    }

    init {
        typeface = Typeface.MONOSPACE
        setBackgroundColor(Color.rgb(43, 43, 43))
        if (isInEditMode) {
            val func = "init"
            appendLog(LogLevel.ASSERT, TAG, func, "assert")
            appendLog(LogLevel.DEBUG, TAG, func, "debug")
            appendLog(LogLevel.ERROR, TAG, func, "error")
            appendLog(LogLevel.INFO, TAG, func, "info")
            appendLog(LogLevel.VERBOSE, TAG, func, "verbose")
            appendLog(LogLevel.WARRING, TAG, func, "warring")
        }
    }

    var simple = false

    fun appendLog(level: LogLevel, tag: String, func: String, msg: String) {
        val spannableString =
            SpannableString(if (simple) msg else "${getLevelChar(level)}/$tag: $func: $msg")
        val foregroundColorSpan = ForegroundColorSpan(
            Color.parseColor(
                when (level) {
                    LogLevel.ASSERT -> "#A364B0"
                    LogLevel.DEBUG -> "#2196F3"
                    LogLevel.ERROR -> "#F44336"
                    LogLevel.INFO -> "#4CAF50"
                    LogLevel.VERBOSE -> "#BBBBBB"
                    LogLevel.WARRING -> "#FFC107"
                }
            )
        )
        spannableString.setSpan(
            foregroundColorSpan, 0, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        append(spannableString)
        append("\n")
    }

    private fun getLevelChar(level: LogLevel): Char {
        return when (level) {
            LogLevel.ASSERT -> 'A'
            LogLevel.DEBUG -> 'D'
            LogLevel.ERROR -> 'E'
            LogLevel.INFO -> 'I'
            LogLevel.VERBOSE -> 'V'
            LogLevel.WARRING -> 'W'
        }
    }
}