package com.dokiwa.dokidoki.timeline.personal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.timeline.api.TimelinePage
import com.dokiwa.dokidoki.timeline.home.EXTRA_KEY
import com.dokiwa.dokidoki.timeline.home.InnerPageFragment
import com.dokiwa.dokidoki.timeline.personal.UserTimelineListActivity.Companion.EXTRA_USER_ID
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_timeline_create.*

private const val TAG = "UserTimelineListActivity"

class UserTimelineListActivity : TranslucentActivity() {

    companion object {
        internal const val EXTRA_USER_ID = "extra.user_id"
        private const val EXTRA_USER_NAME = "extra.user_name"

        fun launch(context: Context, userId: String, userName: String) {
            context.startActivity(
                Intent(context, UserTimelineListActivity::class.java)
                    .putExtra(EXTRA_USER_ID, userId)
                    .putExtra(EXTRA_USER_NAME, userName)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_user_timeline_list)
        initView()
    }

    private fun initView() {
        toolBar.title.text = intent.getStringExtra(EXTRA_USER_NAME)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ListFragment().apply {
                arguments = intent.extras.also { b ->
                    b.putInt(EXTRA_KEY, 0x0001)
                }
            }).commitNow()
    }
}

internal class ListFragment : InnerPageFragment() {
    override fun onGetApiSingle(map: Map<String, String?>): Single<TimelinePage> {
        return Api.get(TimelineApi::class.java).getUserTimeline(map = map.toMutableMap().apply {
            put("user_id", arguments?.getString(EXTRA_USER_ID, ""))
        })
    }
}
