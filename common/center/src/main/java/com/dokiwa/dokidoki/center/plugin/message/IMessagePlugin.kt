package com.dokiwa.dokidoki.center.plugin.message

import android.content.Context
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import io.reactivex.Observable

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

    // 登录 IM 账号
    fun loginIM()

    // 登出 IM 账号
    fun logoutIM()

    // 进入一对一聊天室
    fun launchChatRoom(context: Context, userProfile: UserProfile)

    // 订阅纬度消息数量
    fun subscribeUnreadMsgCount(): Observable<Int>
}