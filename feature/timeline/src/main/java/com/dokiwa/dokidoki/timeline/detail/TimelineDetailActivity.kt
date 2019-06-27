package com.dokiwa.dokidoki.timeline.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import kotlinx.android.synthetic.main.activity_timeline_detail.*

class TimelineDetailActivity : TranslucentActivity() {

    companion object {
        private const val EXTRA_TIMELINE = "extra.timeline"

        fun launch(context: Context, data: Timeline) {
            context.startActivity(
                Intent(context, TimelineDetailActivity::class.java).putExtra(EXTRA_TIMELINE, data)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_timeline_detail)

        toolBar.rightIconView.setOnClickListener {
            toast("举报、删除动态")
        }

        loadData()
    }

    private fun ensureEditAble(userId: Int): Boolean {
        return IProfilePlugin.get().getLoginUserProfile()?.userId == userId
    }

    private fun loadData() {
        val timeline: Timeline? = intent.getParcelableExtra(EXTRA_TIMELINE)

    }

    private fun setData(timeline: Timeline) {

    }
}
