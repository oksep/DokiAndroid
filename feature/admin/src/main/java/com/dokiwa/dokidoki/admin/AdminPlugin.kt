package com.dokiwa.dokidoki.admin

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import com.dokiwa.dokidoki.admin.util.ShakeHelper
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin

/**
 * Created by Septenary on 2018/10/24.
 */
class AdminPlugin : IAdminPlugin {
    override fun launchAdmin(context: Context) {
        context.startActivity(Intent(context, AdminActivity::class.java))
    }

    override fun attachShakeAdmin(lifecycle: Lifecycle) {
        ShakeHelper.attach(lifecycle)
    }
}