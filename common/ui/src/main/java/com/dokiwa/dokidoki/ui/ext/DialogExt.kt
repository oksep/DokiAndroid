package com.dokiwa.dokidoki.ui.ext

import android.app.Dialog
import android.os.Build
import android.view.*
import androidx.annotation.IdRes
import com.dokiwa.dokidoki.ui.R

fun Dialog.showOnBottomWithAnim() {
    show()
    window?.setGravity(Gravity.BOTTOM)
    window?.setWindowAnimations(R.style.BottomInAnim)
}

fun Dialog.showOnBottom() {
    showOnBottom(ViewGroup.LayoutParams.WRAP_CONTENT)
}

/**
 * 在底部显示dialog
 */
fun Dialog.showOnBottom(windowHeight: Int) {
    show()
    val dm = context.applicationContext.resources.displayMetrics
    val params = window!!.attributes
    params.width = dm.widthPixels
    params.height = windowHeight
    window?.attributes = params
    window?.setGravity(Gravity.BOTTOM)
}

fun Dialog.showOnBottomWithAnim(windowHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
    showOnBottom(windowHeight)
    window?.setWindowAnimations(R.style.BottomInAnim)
}

fun <T : View> Dialog.bind(@IdRes res: Int): Lazy<T> {
    return lazy { findViewById<T>(res) }
}

fun Dialog.fitAndroidPNotch() {
    val localWindow = window
    if (Build.VERSION.SDK_INT >= 28 && localWindow != null) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val lp = localWindow.attributes
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        localWindow.attributes = lp

        val decorView = localWindow.decorView
        var systemUiVisibility = decorView.systemUiVisibility
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN)
        systemUiVisibility = systemUiVisibility or flags
        localWindow.decorView.systemUiVisibility = systemUiVisibility
    }
}
