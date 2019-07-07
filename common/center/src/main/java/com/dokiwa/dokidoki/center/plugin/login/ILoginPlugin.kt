package com.dokiwa.dokidoki.center.plugin.login

import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta
import com.dokiwa.dokidoki.center.plugin.model.UserProfile

/**
 * Created by Septenary on 2018/10/24.
 *
 * # 登录流程有两套系统
 * 1. 一套 doki 自有的 账号体系
 * 2. 一套 NIM 账号体系
 *
 * # 在完成 doki 账号登陆后，需要再登录 NIM 账号体系
 *
 * - 本模块 (LoginPlugin) 仅为 doki 账号模块
 * - 消息模块 (MessagePlugin) 提供 NIM 登录相关接口
 */
@PluginImplMeta("com.dokiwa.dokidoki.login.LoginPlugin")
interface ILoginPlugin : FeaturePlugin {
    fun launchLoginActivity(context: Context)

    // 登录用户的 user id
    fun getLoginUserId(): Int?

    // 登录用户的 uuid
    fun getLoginUserUUID(): String?

    // doki 账号登陆成功会返回相关token， nimToken 是向 NIM 登录所提供的
    fun getLoginUserNimToken(): String?

    // 检查本地是否有登录信息，如果没有引导到登录页面
    fun ensureLogin(context: Context)

    // 登出 (包括： doki 账号，NIM 账号)
    fun logOut(context: Context)

    // 启动绑定手机号界面
    fun launchBindPhoneActivity(context: Context)

    // 该方法在登录 doki 账号系统完成时调用
    // 该方法的实现，需要考虑 调用 message plugin 来登录 NIM 账号系统
    fun onLoginDokiComplete(profile: UserProfile, userToken: Parcelable?)

    // 刷新登录用户信息
    fun updateUserProfile(profile: UserProfile)

    fun loginTestingAccount(context: BaseActivity, phoneNumber: String, verifyCode: String)

    companion object {
        fun get(): ILoginPlugin {
            return FeaturePlugin.get(ILoginPlugin::class.java)
        }
    }
}