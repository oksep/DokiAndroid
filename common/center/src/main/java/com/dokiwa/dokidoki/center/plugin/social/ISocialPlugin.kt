package com.dokiwa.dokidoki.center.plugin.social

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2019/1/2.
 */
@PluginImplMeta("com.dokiwa.dokidoki.social.SocialPlugin")
interface ISocialPlugin : FeaturePlugin {

    enum class SocialType { QQ, Wechat, Weibo }

    fun auth(context: Context, socialType: SocialType)

    companion object {
        fun get(): ISocialPlugin {
            return FeaturePlugin.get(ISocialPlugin::class.java)
        }
    }
}