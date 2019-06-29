package com.dokiwa.dokidoki.center.ext

import android.app.Activity
import android.content.Context

/**
 * Created by Septenary on 2019-06-29.
 */

fun Context.isContextAvailable(): Boolean {
    return if (this is Activity) {
        !isDestroyed && !isFinishing
    } else {
        true
    }
}