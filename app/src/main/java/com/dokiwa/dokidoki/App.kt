package com.dokiwa.dokidoki

import android.app.Application
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.util.AppUtil
import com.dokiwa.dokidoki.social.SocialHelper

/**
 * Created by Septenary on 2018/10/24.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        AppCenter.init(this)
        if (AppUtil.isMainProcess(this)) {
            FeaturePlugin.initMainProcessPlugin(this)
            SocialHelper.initSocial(
                context = this,
                qqAppId = BuildConfig.QQ_ID,
                wxAppId = BuildConfig.WECHAT_KEY,
                wxSecretId = BuildConfig.WECHAT_SECRET,
                wbAppId = BuildConfig.WEIBO_KEY,
                wbRedirectUrl = BuildConfig.WEIBO_REDIRECT_URL
            )
        }
        FeaturePlugin.initMultiProcessPlugin(this)
    }
}