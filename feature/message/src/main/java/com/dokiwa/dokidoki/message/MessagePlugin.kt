package com.dokiwa.dokidoki.message

import android.content.Context
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.message.home.MessageFragment
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.util.NIMUtil

/**
 * Created by Septenary on 2018/10/24.
 * NIM 模块
 */

private const val TAG = "MessagePlugin"

class MessagePlugin : IMessagePlugin {

    override fun onInit(context: Context) {
        Log.d(TAG, "init nim sdk")

        // 初始化 NIM 客户端
        NIMClient.init(context, loginInfo(), options())

        // 主进程中注册相关监听器
        if (NIMUtil.isMainProcess(context)) {
            registerObservers()
        }
    }

    override fun obtainHomeMessageFragment(): Fragment {
        return MessageFragment()
    }

    private fun loginInfo(): LoginInfo? {
        return NIMSP.getNIMLoginInfo()
    }

    private fun options(): SDKOptions? {
        return SDKOptions.DEFAULT.apply {
            asyncInitSDK = true
            reducedIM = true
        }
    }

    private fun registerObservers() {
        NIMSDK.getAuthServiceObserve().observeOnlineStatus({ status ->
            Log.d(TAG, "online status -> $status")
            when (status) {
                StatusCode.KICKOUT, StatusCode.KICK_BY_OTHER_CLIENT -> { // 被踢出，引导到 重新登录页面
                    ILoginPlugin.get().logOut(AppCenter.get().context)
                }
                else -> {
                    // noop
                }
            }
        }, true)
    }

    override fun loginNIM() {
        val future = NIMSDK.getAuthService().login(loginInfo())
        future.setCallback(object : RequestCallback<LoginInfo> {
            override fun onSuccess(loginInfo: LoginInfo) {
                Log.d(TAG, "login success -> $loginInfo")
                // NIM 登录成功后缓存一份 loginInfo
                // 下次 app 启动时，在初始化 NIMClient 时会尝试用 loginInfo 登录 NIM
                NIMSP.saveNIMLoginInfo(loginInfo)
                registerObservers()
            }

            override fun onFailed(code: Int) {
                Log.w(TAG, "login failed -> $code")
            }

            override fun onException(exception: Throwable?) {
                Log.w(TAG, "login exception", exception)
            }

        })
    }

    override fun logoutNIM() {
        Log.w(TAG, "log out nim")
        NIMSP.clearNIMLoginInfo()
        NIMSDK.getAuthService().logout()
    }
}