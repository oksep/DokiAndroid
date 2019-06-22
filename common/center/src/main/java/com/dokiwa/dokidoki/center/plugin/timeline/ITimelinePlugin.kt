package com.dokiwa.dokidoki.center.plugin.timeline

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2019/6/22.
 */

@PluginImplMeta("com.dokiwa.dokidoki.timeline.TimelinePlugin")
interface ITimelinePlugin : FeaturePlugin {

    fun test(context: Context)

    companion object {
        fun get(): ITimelinePlugin {
            return FeaturePlugin.get(ITimelinePlugin::class.java)
        }
    }
}