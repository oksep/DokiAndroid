package com.dokiwa.dokidoki.social

import android.content.Context
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.center.plugin.social.ISocialPlugin
import com.dokiwa.dokidoki.social.auth.SocialAuth
import com.dokiwa.dokidoki.social.auth.impl.QQAuth
import com.dokiwa.dokidoki.social.auth.impl.WechatAuth
import com.dokiwa.dokidoki.social.auth.impl.WeiboAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 * Created by Septenary on 2019/1/3.
 */
object Social {

    init {
        AppCenter.get().apply {
            config.apply {
                wechatKey = WX_KEY
                wechatSecret = WX_SECRET
                qqKey = QQ_KEY
                qqSecret = QQ_SECRET
                weiboKey = WEIBO_KEY
                weiboSecret = WEIBO_SECRET
            }
        }
    }

    private val config = Config()

    private var wechatApi: IWXAPI? = null

    internal fun getWechatApi(context: Context): IWXAPI {
        if (config.wechatKey == null) {
            throw IllegalStateException("wechat key&secret not config")
        }
        if (wechatApi == null) {
            wechatApi = WXAPIFactory.createWXAPI(context, config.wechatKey, true).apply {
                registerApp(config.wechatKey)
            }
        }
        return wechatApi!!
    }

    internal fun getSocialAuth(type: ISocialPlugin.SocialType): SocialAuth {
        return when (type) {
            ISocialPlugin.SocialType.QQ -> QQAuth(config.qqKey!!, config.qqSecret!!)
            ISocialPlugin.SocialType.Wechat -> WechatAuth(config.wechatKey!!, config.wechatSecret!!)
            ISocialPlugin.SocialType.Weibo -> WeiboAuth(config.weiboKey!!, config.weiboSecret!!)
        }
    }

    internal class Config {
        var wechatKey: String? = null
        var wechatSecret: String? = null
        var qqKey: String? = null
        var qqSecret: String? = null
        var weiboKey: String? = null
        var weiboSecret: String? = null
    }
}