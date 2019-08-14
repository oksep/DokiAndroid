package com.dokiwa.dokidoki.timeline.notify

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toUriAndResolveDeepLink
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Notification
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.util.LineDivider
import kotlinx.android.synthetic.main.activity_timeline_notify_list.*
import kotlinx.android.synthetic.main.view_item_timeline_notify.view.*

private const val TAG = "TimelineNotifyListActivity"

class TimelineNotifyListActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(
                Intent(context, TimelineNotifyListActivity::class.java)
            )
        }
    }

    private val adapter by lazy { PoiAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_notify_list)
        refreshRecyclerView.getRecyclerView().setBackgroundColor(Color.WHITE)
        refreshRecyclerView.getRecyclerView().addItemDecoration(
            LineDivider(
                resources,
                R.color.line_light,
                R.dimen.timeline_notify_divider_height,
                R.dimen.timeline_notify_divider_left,
                R.dimen.timeline_notify_divider_right
            )
        )
        refreshRecyclerView.getRefreshLayout().setRefreshListenerHaptic {
            loadData()
        }
        refreshRecyclerView.setAdapter(adapter)

        adapter.setOnItemClickListener { adapter, view, position ->
            (adapter.getItem(position) as? Notification)?.let {
                it.uri.toUriAndResolveDeepLink(this, false)
            }
        }
        loadData()
    }

    private fun loadData() {
        refreshRecyclerView.showLoading()
        Api.get(TimelineApi::class.java)
            .getNotification()
            .subscribeApi(this, {
                Log.d(TAG, "get timeline success -> $it")
                adapter.setNewData(it.list)
                refreshRecyclerView.showSuccess()
            }, {
                Log.e(TAG, "get timeline failed", it)
                refreshRecyclerView.showError(R.drawable.ui_ic_oops_illegal, R.string.load_failed)
            })
    }
}

class PoiAdapter : BaseQuickAdapter<Notification, BaseViewHolder>(R.layout.view_item_timeline_notify, null) {
    override fun convert(helper: BaseViewHolder, item: Notification) {
        helper.itemView.avatar.glideAvatar(item.senderUser)
        helper.itemView.name.text = item.title
        helper.itemView.desc.text = item.content
        helper.itemView.time.text = item.createdAt.toReadable()
    }
}
