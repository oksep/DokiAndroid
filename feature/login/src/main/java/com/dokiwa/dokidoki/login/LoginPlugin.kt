package com.dokiwa.dokidoki.login

import android.annotation.SuppressLint
import android.content.Context
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.toUriAndResolveDeepLink
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

    override fun launchBindPhoneActivity(context: Context) {
        "dokidoki://dokiwa.com/me/bind_phone".toUriAndResolveDeepLink(context, false)
    }

    override fun logOut(context: Context) {
        LoginSP.clearUserToken()
        Api.resetAuthenticationToken(null, null)
        launchLoginActivity(context)
    }

    override fun ensureLogin(context: Context) {
        // 没有 token，跳转到 登录页
        if (LoginSP.getUserToken() == null) {
            launchLoginActivity(context)
        }
    }

    @SuppressLint("CheckResult")
    private fun registerAuthentication(context: Context) {

        // 初始化认证 token
        val userToken = LoginSP.getUserToken()
        if (userToken != null) {
            Api.resetAuthenticationToken(userToken.macKey, userToken.accessToken)
        }

        // 用户认证失败重新登录
        Api.unAuthenticationSubject.delay(2, TimeUnit.SECONDS).subscribe {
            Api.resetAuthenticationToken(null, null)
            if (LoginSP.getUserToken() != null) {
                LoginSP.clearUserToken()
                launchLoginActivity(context)
            }
        }
    }
}