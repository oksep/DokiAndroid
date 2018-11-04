package com.dokiwa.dokidoki.ui.ext

import android.content.Context
import android.content.res.Resources

/**
 * Created by Septenary on 2018/11/4.
 */

fun Context.dp2px(dp: Float): Int {
    return this.dp2pxExact(dp).toInt()
}

fun Context.dp2pxExact(dp: Float): Float {
    val scale = resources.displayMetrics.density
    return dp * scale + 0.5f
}

fun Context.sp2px(spValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}