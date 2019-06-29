package com.dokiwa.dokidoki.center.plugin.update

import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2018/10/24.
 */
@PluginImplMeta("com.dokiwa.dokidoki.update.UpdatePlugin")
interface IUpdatePlugin : FeaturePlugin {

    companion object {
        fun get(): IUpdatePlugin {
            return FeaturePlugin.get(IUpdatePlugin::class.java)
        }
    }
}