package com.dokiwa.dokidoki.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toUriAndResolveDeepLink
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.login.activity.LoginActivity
import com.dokiwa.dokidoki.login.activity.ToHomeUtil
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2018/10/24.
 */
class LoginPlugin : ILoginPlugin {

    override fun onInit(context: Context) {
        registerAuthentication(context)
    }

    override fun getLoginUserId(): Int? {
        return FeaturePlugin.get(IProfilePlugin::class.java).getLoginUserProfile()?.userId
    }

    override fun launchLoginActivity(context: Context) {
        LoginActivity.launch(context)
    }

    override fun launchBindPhoneActivity(context: Context) {
        "dokidoki://dokiwa.com/me/bind_phone".toUriAndResolveDeepLink(context, false)
    }

    override fun saveLoginUserToken(userToken: Parcelable?) {
        if (userToken is UserToken) {
            LoginSP.saveUserToken(userToken)
        }
    }

    override fun logOut(context: Context) {
        // token
        LoginSP.clearUserToken()

        // api
        Api.resetAuthenticationToken(null, null)

        // user profile
        FeaturePlugin.get(IProfilePlugin::class.java).clearUserProfile()

        // to login activity
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

    override fun loginTestingAccount(context: BaseActivity, phoneNumber: String, verifyCode: String) {
        Api.get(LoginApi::class.java)
            .loginByVerifyCode(phoneNumber, verifyCode)
            .subscribeApi(
                context,
                { ToHomeUtil.ensureProfileThenToHome(it, context) }
            )
    }
}