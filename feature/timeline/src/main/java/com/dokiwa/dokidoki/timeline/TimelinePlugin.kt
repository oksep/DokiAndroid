package com.dokiwa.dokidoki.timeline

import android.content.Context
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin

/**
 * Created by Septenary on 2019-06-22.
 */
class TimelinePlugin : ITimelinePlugin {
    override fun test(context: Context) {
        context.toast("WOOOOOO")
    }

}