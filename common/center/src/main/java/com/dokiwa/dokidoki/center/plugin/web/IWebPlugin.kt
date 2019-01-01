package com.dokiwa.dokidoki.center.plugin.web

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2019/1/2.
 */
@PluginImplMeta("com.dokiwa.dokidoki.web.WebPlugin")
interface IWebPlugin : FeaturePlugin {

    fun launchWebActivity(context: Context, url: String)

    companion object {
        fun get(): IWebPlugin {
            return FeaturePlugin.get(IWebPlugin::class.java)
        }
    }
}