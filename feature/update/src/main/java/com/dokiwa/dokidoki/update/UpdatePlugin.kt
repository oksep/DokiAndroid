package com.dokiwa.dokidoki.update

import android.content.Context
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.update.IUpdatePlugin

/**
 * Created by Septenary on 2018/10/24.
 */
class UpdatePlugin : IUpdatePlugin {
    override fun onInit(context: Context) {
        super.onInit(context)
        context.toast("AAAAAAAAAAA")
    }
}