package com.dokiwa.dokidoki.center.plugin.message

import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2018/10/24.
 */
@PluginImplMeta("com.dokiwa.dokidoki.message.MessagePlugin")
interface IMessagePlugin : FeaturePlugin {

    companion object {
        fun get(): IMessagePlugin {
            return FeaturePlugin.get(IMessagePlugin::class.java)
        }
    }

    // 首页 -> 消息页
    fun obtainHomeMessageFragment(): Fragment

    // 登录 NIM 账号
    fun loginNIM()

    // 登出 NIM 账号
    fun logoutNIM()
}