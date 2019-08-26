package com.dokiwa.dokidoki.timeline

import android.app.Activity
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.timeline.api.TimelinePage
import com.dokiwa.dokidoki.timeline.create.CreateTimelineActivity
import com.dokiwa.dokidoki.timeline.home.TimelineFragment
import com.dokiwa.dokidoki.timeline.personal.UserTimelineListActivity
import io.reactivex.Single

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

    override fun launchUserTimelineActivity(context: Activity, userId: String, userName: String) {
        UserTimelineListActivity.launch(context, userId, userName)
    }

    override fun getUserTimelineThumbs(userId: String): Single<List<String>> {
        return Api.get(TimelineApi::class.java)
            .getUserTimeline(map = mapOf("user_id" to userId))
            .map { page: TimelinePage ->
                var list = listOf<String>()
                page.timelineList.forEach { timeline ->
                    list = list + (timeline.pictureList?.map { it.adaptUrl() }?.toList() ?: listOf())
                }
                list
            }
            .onErrorReturn { listOf() }
    }
}