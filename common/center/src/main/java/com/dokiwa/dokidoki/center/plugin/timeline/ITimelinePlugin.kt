package com.dokiwa.dokidoki.center.plugin.timeline

import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2019/6/22.
 */

@PluginImplMeta("com.dokiwa.dokidoki.timeline.TimelinePlugin")
interface ITimelinePlugin : FeaturePlugin {

    fun obtainHomeTimelineFragment(): Fragment

    companion object {
        fun get(): ITimelinePlugin {
            return FeaturePlugin.get(ITimelinePlugin::class.java)
        }
    }
}