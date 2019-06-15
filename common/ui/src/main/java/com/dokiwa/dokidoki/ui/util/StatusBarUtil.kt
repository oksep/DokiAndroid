package com.dokiwa.dokidoki.ui.util

import android.app.Activity
import qiu.niorgai.StatusBarCompat

object StatusBarUtil {
    fun test(activity: Activity) {
        StatusBarCompat.translucentStatusBar(activity)
    }
}