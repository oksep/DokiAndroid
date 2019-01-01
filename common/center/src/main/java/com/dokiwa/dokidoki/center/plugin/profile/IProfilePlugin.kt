package com.dokiwa.dokidoki.center.plugin.profile

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2019/1/2.
 */
@PluginImplMeta("com.dokiwa.dokidoki.profile.ProfilePlugin")
interface IProfilePlugin : FeaturePlugin {

    fun launchProfileActivity(context: Context)

    companion object {
        fun get(): IProfilePlugin {
            return FeaturePlugin.get(IProfilePlugin::class.java)
        }
    }
}