package com.dokiwa.dokidoki.admin

import android.content.Context
import android.content.Intent
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin

/**
 * Created by Septenary on 2018/10/24.
 */
class AdminPlugin : IAdminPlugin {
    override fun launchAdmin(context: Context) {
        context.startActivity(Intent(context, AdminActivity::class.java))
    }
}