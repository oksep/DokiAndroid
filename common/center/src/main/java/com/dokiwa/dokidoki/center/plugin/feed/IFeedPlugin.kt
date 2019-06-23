package com.dokiwa.dokidoki.center.plugin.feed

import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2018/10/24.
 */
@PluginImplMeta("com.dokiwa.dokidoki.feed.FeedPlugin")
interface IFeedPlugin : FeaturePlugin {

    fun obtainHomeFeedFragment(): Fragment

    companion object {
        fun get(): IFeedPlugin {
            return FeaturePlugin.get(IFeedPlugin::class.java)
        }
    }
}