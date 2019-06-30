package com.dokiwa.dokidoki.update

import android.app.Activity
import com.dokiwa.dokidoki.center.plugin.update.IUpdatePlugin

class UpdatePlugin : IUpdatePlugin {
    override fun checkUpdate(context: Activity) {
        UpdateManager.checkUpdate(context)
    }
}