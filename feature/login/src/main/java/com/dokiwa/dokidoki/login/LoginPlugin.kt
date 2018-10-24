package com.dokiwa.dokidoki.login

import android.content.Context
import android.content.Intent
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin

/**
 * Created by Septenary on 2018/10/24.
 */
class LoginPlugin : ILoginPlugin {
    override fun launchLoginActivity(context: Context) {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
}