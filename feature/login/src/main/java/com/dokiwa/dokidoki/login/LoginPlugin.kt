package com.dokiwa.dokidoki.login

import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toUriAndResolveDeepLink
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.login.activity.LoginActivity
import com.dokiwa.dokidoki.login.activity.ToHomeUtil
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken
import io.reactivex.disposables.Disposable

/**
 * Created by Septenary on 2018/10/24.
 */
class LoginPlugin : ILoginPlugin {

    override fun onInit(context: Context) {
        registerAuthentication(context)
    }

    override fun launchLoginActivity(context: Context) {
        LoginActivity.launch(context)
    }

    override fun launchBindPhoneActivity(context: Context) {
        "dokidoki://dokiwa.com/me/bind_phone".toUriAndResolveDeepLink(context, false)
    }

    override fun getLoginUserId(): Int? {
        return LoginSP.getUserProfile()?.userId
    }

    override fun getLoginUserUUID(): String? {
        return LoginSP.getUserProfile()?.uuid
    }

    override fun getLoginUserNimToken(): String? {
        return LoginSP.getUserToken()?.nimToken
    }

    override fun saveLoginUser(profile: UserProfile, userToken: Parcelable?) {
        if (userToken is UserToken) {
            LoginSP.saveUserToken(userToken)
        }

        LoginSP.saveUserProfile(profile)
    }

    override fun updateUserProfile(profile: UserProfile) {
        LoginSP.saveUserProfile(profile)
    }

    override fun logOut(context: Context) {
        // token
        LoginSP.clearLoginUser()

        // api
        Api.resetAuthenticationToken(null, null)

        // to login activity
        launchLoginActivity(context)
    }

    override fun ensureLogin(context: Context) {
        // 没有 token 或者 用户信息，跳转到 登录页
        if (LoginSP.getUserToken() == null || LoginSP.getUserProfile() == null) {
            launchLoginActivity(context)
        }
    }

    private fun registerAuthentication(context: Context) {
        // 初始化认证 token
        val userToken = LoginSP.getUserToken()
        if (userToken != null) {
            Api.resetAuthenticationToken(userToken.macKey, userToken.accessToken)
        }

        // 用户认证失败重新登录
        var dispose: Disposable? = null
        dispose = Api.unAuthenticationSubject.subscribe {
            // 取消订阅
            if (dispose?.isDisposed == false) {
                dispose?.dispose()
                dispose = null
            }

            // 重置 api token
            Api.resetAuthenticationToken(null, null)

            // 跳转到登录页
            LoginSP.clearLoginUser()
            launchLoginActivity(context)
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