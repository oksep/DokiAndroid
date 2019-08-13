package com.dokiwa.dokidoki.timeline.notify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Notification
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import kotlinx.android.synthetic.main.activity_timeline_notify_list.*

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
        refreshRecyclerView.getRefreshLayout().setRefreshListenerHaptic {
            loadData()
        }
        refreshRecyclerView.setAdapter(adapter)
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

    }
}
