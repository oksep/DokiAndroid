package com.dokiwa.dokidoki.social

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.social.ISocialPlugin


/**
 * Created by Septenary on 2019/1/3.
 */
class SocialPlugin : ISocialPlugin {

    override fun auth(context: Context, socialType: ISocialPlugin.SocialType) {
        Social.getSocialAuth(socialType).auth(context)
    }
}