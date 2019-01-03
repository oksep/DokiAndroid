package com.dokiwa.dokidoki.social.auth.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.social.Social
import com.dokiwa.dokidoki.social.auth.SocialAuth
import com.tencent.mm.opensdk.modelmsg.SendAuth
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Septenary on 2019/1/3.
 */
const val ACTION_WECHAT_AUTH = "action.wechat.auth"
const val EXTRA_AUTH_CODE = "extra.wechat.auth_code"

private const val WECHAT_API_BASE_URL = "https://api.weixin.qq.com/"

internal class WechatAuth(
    private val wechatKey: String,
    private val wechatSecret: String
) : SocialAuth {

    override fun auth(context: Context) {
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wechat_sdk_login_${System.currentTimeMillis()}"
        Social.getWechatApi(context).sendReq(req)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val code = intent?.getStringExtra(EXTRA_AUTH_CODE)
                if (code != null) {
                    getAccessToken(code)
                }
            }
        }

        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(ACTION_WECHAT_AUTH))
    }

    interface WechatAuthApi {
        @GET("/sns/oauth2/access_token")
        fun getAccessToken(
            @Query("appid") appId: String,
            @Query("secret") secret: String,
            @Query("code") code: String,
            @Query("grant_type") type: String = "authorization_code"
        ): Single<JSONObject>

        @GET("/sns/userinfo")
        fun getUserInfo(
            @Query("openid") openId: String,
            @Query("access_token") accessToken: String
        ): Single<JSONObject>
    }

    private fun getAccessToken(code: String) {
        Api.getSimpleClient(WechatAuthApi::class.java, WECHAT_API_BASE_URL)
            .getAccessToken(
                appId = wechatKey, // TODO: 2019/1/4 @Septenary
                secret = wechatSecret, // TODO: 2019/1/4 @Septenary
                code = code
            ).subscribeApi(null, {
                val openId = it.getString("openid")
                val accessToken = it.getString("access_token")
                getUserInfo(openId, accessToken)
            })
    }

    private fun getUserInfo(openId: String, accessToken: String) {
        Api.getSimpleClient(WechatAuthApi::class.java, WECHAT_API_BASE_URL)
            .getUserInfo(openId, accessToken).subscribeApi(null, { jsonObject ->
                val openId = jsonObject.getString("openid")
                val nickname = jsonObject.getString("nickname")
                val avatar = jsonObject.getString("headimgurl")
                val unionid = jsonObject.getString("unionid")
                val gender = if (jsonObject.getInt("sex") == 1) "male" else "female"
            })
    }
}