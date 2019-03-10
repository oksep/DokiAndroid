package com.dokiwa.dokidoki.center.plugin.login

import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2018/10/24.
 */
@PluginImplMeta("com.dokiwa.dokidoki.login.LoginPlugin")
interface ILoginPlugin : FeaturePlugin {
    fun launchLoginActivity(context: Context)

    fun getLoginUserId(): Int?

    fun ensureLogin(context: Context)

    fun logOut(context: Context)

    fun launchBindPhoneActivity(context: Context)

    fun saveLoginUserToken(userToken: Parcelable?)

    fun loginTestingAccount(context: BaseActivity, phoneNumber: String, verifyCode: String)

    companion object {
        fun get(): ILoginPlugin {
            return FeaturePlugin.get(ILoginPlugin::class.java)
        }
    }
}