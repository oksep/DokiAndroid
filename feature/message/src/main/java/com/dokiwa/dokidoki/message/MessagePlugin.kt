package com.dokiwa.dokidoki.message

import android.content.Context
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.message.home.MessageFragment
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.NIMSDK
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.LoginInfo

/**
 * Created by Septenary on 2018/10/24.
 */

private const val TAG = "MessagePlugin"

class MessagePlugin : IMessagePlugin {

    override fun obtainHomeMessageFragment(): Fragment {
        return MessageFragment()
    }

    override fun onInit(context: Context) {
        NIMClient.init(context, loginInfo(), options())
    }

    private fun config() {

    }

    private fun loginInfo(): LoginInfo? {
        val uuid = ILoginPlugin.get().getLoginUserUUID()
        val nimToken = ILoginPlugin.get().getLoginUserNimToken()
        return if (uuid.isNullOrEmpty() || nimToken.isNullOrEmpty()) {
            null
        } else {
            LoginInfo(uuid, nimToken, BuildConfig.IM_KEY)
        }
    }

    private fun options(): SDKOptions? {
        return SDKOptions.DEFAULT.apply {
            asyncInitSDK = true
            reducedIM = true
        }
    }

    override fun loginNIM() {
        val future = NIMSDK.getAuthService().login(loginInfo())
        future.setCallback(object : RequestCallback<LoginInfo> {
            override fun onSuccess(param: LoginInfo?) {
                Log.d(TAG, "login success -> $param")
            }

            override fun onFailed(code: Int) {
                Log.d(TAG, "login failed -> $code")
            }

            override fun onException(exception: Throwable?) {
                Log.e(TAG, "login exception", exception)
            }

        })
    }

    override fun logoutNIM() {
        NIMSDK.getAuthService().logout()
    }
}