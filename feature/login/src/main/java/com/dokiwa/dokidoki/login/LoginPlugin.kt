package com.dokiwa.dokidoki.login

import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toUriAndResolveDeepLink
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.login.activity.LoginActivity
import com.dokiwa.dokidoki.login.activity.ToHomeUtil
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.EmptyDisposable

/**
 * Created by Septenary on 2018/10/24.
 */
private const val TAG = "LoginPlugin"

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

    override fun onLoginDokiComplete(profile: UserProfile, userToken: Parcelable?) {
        Log.w(TAG, "save login user $profile, $userToken")

        // 保存 user token
        if (userToken is UserToken) {
            LoginSP.saveUserToken(userToken)
        }

        // 保存 账号信息
        LoginSP.saveUserProfile(profile)

        // 登录 NIM
        IMessagePlugin.get().loginNIM()

        // 注册 auth 监听
        registerAuthentication(AppCenter.get().context)
    }

    override fun updateUserProfile(profile: UserProfile) {
        Log.d(TAG, "user profile update -> $profile")
        LoginSP.saveUserProfile(profile)
    }

    override fun logOut(context: Context) {
        Log.w(TAG, "logOut...")

        // nim
        IMessagePlugin.get().logoutNIM()

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
            Log.w(TAG, "ensureLogin -> user token & profile is not available")
            launchLoginActivity(context)
        } else {
            Log.d(TAG, "ensureLogin -> user token & profile is available")
        }
    }

    // 注册 api auth 监听器
    private fun registerAuthentication(context: Context) {
        // 初始化认证 token
        val userToken = LoginSP.getUserToken()
        if (userToken != null) {
            Log.d(TAG, "registerAuthentication -> reset api auth token")
            Api.resetAuthenticationToken(userToken.macKey, userToken.accessToken)

            // 用户认证失败重新登录
            var dispose: Disposable = EmptyDisposable.NEVER
            dispose = Api.unAuthenticationSubject.subscribe {
                Log.w(TAG, "api authentication failed")
                // 取消订阅
                if (dispose.isDisposed) {
                    dispose.dispose()
                }
                // 退登
                logOut(context)
            }
        } else {
            Log.w(TAG, "registerAuthentication -> none auth token")
            Api.resetAuthenticationToken(null, null)
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