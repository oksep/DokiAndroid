package com.dokiwa.dokidoki.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin

/**
 * Created by Septenary on 2018/10/24.
 */
class LoginPlugin : ILoginPlugin {

    override fun onInit(context: Context) {
        registerAuthentication(context)
    }

    @SuppressLint("CheckResult")
    private fun registerAuthentication(context: Context) {
        Api.unAuthenticationSubject.subscribe {
            Log.d("LoginPlugin", "请重新登录")
        }
    }

    override fun launchLoginActivity(context: Context) {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }
}