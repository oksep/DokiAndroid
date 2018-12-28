package com.dokiwa.dokidoki.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.login.UserModel
import com.dokiwa.dokidoki.login.activity.LoginActivity
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2018/10/24.
 */
class LoginPlugin : ILoginPlugin {

    @SuppressLint("CheckResult")
    private fun registerAuthentication(context: Context) {
        Api.unAuthenticationSubject.delay(2, TimeUnit.SECONDS).subscribe {
            Log.d("LoginPlugin", "请重新登录")
            launchLoginActivity(context)
        }
    }

    override fun onInit(context: Context) {
        registerAuthentication(context)
    }

    override fun getUser(): UserModel? {
        return null
    }

    override fun launchLoginActivity(context: Context) {
        context.startActivity(Intent(context, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}