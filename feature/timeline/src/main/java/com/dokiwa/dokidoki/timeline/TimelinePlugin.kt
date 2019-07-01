package com.dokiwa.dokidoki.timeline

import android.app.Activity
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.timeline.create.CreateTimelineActivity
import com.dokiwa.dokidoki.timeline.home.TimelineFragment

/**
 * Created by Septenary on 2019-06-22.
 */
class TimelinePlugin : ITimelinePlugin {

    override fun obtainHomeTimelineFragment(): Fragment {
        return TimelineFragment()
    }

    override fun launchCreateTimelineActivity(context: Activity) {
        CreateTimelineActivity.launch(context)
    }
}