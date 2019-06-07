package com.dokiwa.dokidoki.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager

object NotchHelper {
    fun isNotch(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && (isMiUiNotch() || isHuaWeiNotch(context))
    }

    @SuppressLint("NewApi")
    fun isAndroidPNotch(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val boundingRectList = activity.window?.decorView?.rootWindowInsets?.displayCutout?.boundingRects
            return boundingRectList != null && boundingRectList.isNotEmpty()
        }
        return false
    }

    private fun isMiUiNotch(): Boolean {
        return try {
            SystemPropertiesProxy.getInt("ro.miui.notch", 0) == 1
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun isHuaWeiNotch(context: Context): Boolean {
        var ret = false
        try {
            val cl = context.classLoader
            val hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = hwNotchSizeUtil.getMethod("hasNotchInScreen")
            ret = get.invoke(hwNotchSizeUtil) as Boolean
        } catch (e: ClassNotFoundException) {
            Log.e("test", "hasNotchInScreen ClassNotFoundException")
        } catch (e: NoSuchMethodException) {
            Log.e("test", "hasNotchInScreen NoSuchMethodException")
        } catch (e: Exception) {
            Log.e("test", "hasNotchInScreen Exception")
        } finally {
            return ret
        }
    }

    fun getNotchStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}

fun Activity.fitAndroidPFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window?.let {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val lp = it.attributes
            lp?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            it.attributes = lp

            val decorView = it.decorView
            var systemUiVisibility = decorView.systemUiVisibility
            val flags = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN)
            systemUiVisibility = systemUiVisibility or flags
            it.decorView.systemUiVisibility = systemUiVisibility
        }
    }
}

fun Window.fitAndroidPNotch() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        requestFeature(Window.FEATURE_NO_TITLE)
        val lp = attributes
        lp?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        attributes = lp
    }
}