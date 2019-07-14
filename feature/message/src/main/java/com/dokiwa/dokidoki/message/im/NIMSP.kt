package com.dokiwa.dokidoki.message.im

import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.sp.BaseSharedPreferences
import com.netease.nimlib.sdk.auth.LoginInfo

/**
 * Created by Septenary on 2018/12/31.
 */
object NIMSP : BaseSharedPreferences("message-nim", asUserData = false) {

    private const val KEY_NIM_LOGIN_INFO_ACCOUNT = "key.nim_login_info_account"
    private const val KEY_NIM_LOGIN_INFO_TOKEN = "key.nim_login_info_token"

    fun saveNIMLoginInfo(loginInfo: LoginInfo) {
        save(KEY_NIM_LOGIN_INFO_ACCOUNT, loginInfo.account)
        save(KEY_NIM_LOGIN_INFO_TOKEN, loginInfo.token)
    }

    fun getNIMLoginInfo(): LoginInfo? {
        val account = getString(KEY_NIM_LOGIN_INFO_ACCOUNT)
        val token = getString(KEY_NIM_LOGIN_INFO_TOKEN)
        return if (account.isNullOrEmpty() || token.isNullOrEmpty()) {
            val uuid = ILoginPlugin.get().getLoginUserUUID()
            val nimToken = ILoginPlugin.get().getLoginUserNimToken()
            if (uuid.isNullOrEmpty() || nimToken.isNullOrEmpty()) {
                null
            } else {
                LoginInfo(uuid, nimToken, BuildConfig.IM_KEY)
            }
        } else {
            LoginInfo(account, token, BuildConfig.IM_KEY)
        }
    }

    fun clearNIMLoginInfo() {
        clearAll()
    }
}