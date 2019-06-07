package com.dokiwa.dokidoki.ui.util

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.PopupWindow

private const val TAG = "PopupWindowExtension"

fun PopupWindow.safeShowAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
    if ((parent.context as? Activity)?.isFinishing != true && parent.windowToken != null) {
        showAtLocation(parent, gravity, x, y)
    } else {
        Log.d(TAG, "popupWindow can not show at location")
    }
}

fun PopupWindow.safeShowAsDropDown(anchor: View, xOff: Int, yOff: Int) {
    if ((anchor.context as? Activity)?.isFinishing != true && anchor.windowToken != null) {
        showAsDropDown(anchor, xOff, yOff)
    } else {
        Log.d(TAG, "popupWindow can not show")
    }
}

fun PopupWindow.safeDismiss(parent: View) {
    if ((parent.context as? Activity)?.isFinishing != true && parent.windowToken != null) {
        dismiss()
    } else {
        Log.d(TAG, "popupWindow can not dismiss")
    }
}

fun PopupWindow.safeDismiss(context: Context) {
    if ((context as? Activity)?.isFinishing != true) {
        dismiss()
    } else {
        Log.d(TAG, "popupWindow can not dismiss")
    }
}