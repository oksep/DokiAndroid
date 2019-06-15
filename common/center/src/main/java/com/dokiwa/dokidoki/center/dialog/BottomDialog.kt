package com.dokiwa.dokidoki.center.dialog

import android.app.Activity
import android.view.Gravity
import com.dokiwa.dokidoki.center.R

open class BottomDialog(activity: Activity) : BaseDialog(activity, R.style.BottomDialog) {
    override fun show() {
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.BottomInAnim)
        super.show()
    }
}