package com.dokiwa.dokidoki.center.plugin.location

import androidx.fragment.app.FragmentActivity
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

@PluginImplMeta("com.dokiwa.dokidoki.location.LocationPlugin")
interface ILocationPlugin : FeaturePlugin {

    companion object {
        fun get(): ILocationPlugin {
            return FeaturePlugin.get(ILocationPlugin::class.java)
        }
    }

    fun launchPoiActivity(context: FragmentActivity, onGetPoi: (String, Double, Double) -> Unit)
}