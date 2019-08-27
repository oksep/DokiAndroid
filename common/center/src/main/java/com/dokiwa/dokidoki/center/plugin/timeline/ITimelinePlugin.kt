package com.dokiwa.dokidoki.center.plugin.timeline

import android.app.Activity
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta
import io.reactivex.Single

/**
 * Created by Septenary on 2019/6/22.
 */

@PluginImplMeta("com.dokiwa.dokidoki.timeline.TimelinePlugin")
interface ITimelinePlugin : FeaturePlugin {

    fun obtainHomeTimelineFragment(): Fragment

    fun launchCreateTimelineActivity(context: Activity)

    fun launchUserTimelineActivity(context: Activity, userId: String, userName: String)

    fun getUserTimelineThumbs(userId: String): Single<Pair<Int, List<String>>>

    companion object {
        fun get(): ITimelinePlugin {
            return FeaturePlugin.get(ITimelinePlugin::class.java)
        }
    }
}