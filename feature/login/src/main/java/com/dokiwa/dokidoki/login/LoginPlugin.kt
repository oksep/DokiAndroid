package com.dokiwa.dokidoki.login

import android.annotation.SuppressLint
import android.content.Context
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.login.UserModel
import com.dokiwa.dokidoki.login.activity.LoginActivity
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2018/10/24.
 */
class LoginPlugin : ILoginPlugin {

    override fun onInit(context: Context) {
        registerAuthentication(context)
    }

    override fun getUser(): UserModel? {
        return null
    }

    override fun launchLoginActivity(context: Context) {
        LoginActivity.launch(context)
    }

    @SuppressLint("CheckResult")
    private fun registerAuthentication(context: Context) {

        // 初始化认证 token
        val userToken = LoginSP.getUserToken()
        Api.resetAuthenticationToken(
            macKey = userToken?.macKey,
            accessToken = userToken?.accessToken
        )

        // 用户认证失败重新登录
        Api.unAuthenticationSubject.delay(2, TimeUnit.SECONDS).subscribe {
            launchLoginActivity(context)
        }
    }
}