package com.dokiwa.dokidoki.center.plugin.home

import android.app.Activity
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2018/10/24.
 */

@PluginImplMeta("com.dokiwa.dokidoki.home.HomePlugin")
interface IHomePlugin : FeaturePlugin {

    fun launchHomeActivity(context: Activity)

    companion object {
        fun get(): IHomePlugin {
            return FeaturePlugin.get(IHomePlugin::class.java)
        }
    }
}