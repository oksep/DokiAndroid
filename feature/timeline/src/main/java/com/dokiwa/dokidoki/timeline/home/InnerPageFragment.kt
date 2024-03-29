package com.dokiwa.dokidoki.timeline.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import com.dokiwa.dokidoki.timeline.api.TimelinePage
import com.dokiwa.dokidoki.timeline.comment.TimelineCommentActivity
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_timeline_inner.*

private const val TAG = "InnerPageFragment"

const val EXTRA_KEY = "extra.view_model.key"

internal abstract class InnerPageFragment : BaseShareFragment(R.layout.fragment_timeline_inner) {

    private val adapter by lazy { TimelineAdapter(this, ::onMoreClick) }

    private fun ensureModel(): TimelineViewModel {
        val key = arguments?.getInt(EXTRA_KEY) ?: 0
        return (getModel<TimelineViewModel>(key) ?: TimelineViewModel()).also {
            putModel(key, it)
        }
    }

    private val data: MutableList<TimelinePage>
        get() = ensureModel().timelinePages

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshRecyclerView()
        ensureData()
    }

    private fun initRefreshRecyclerView() {
        refreshRecyclerView.enhancedSlidingSensitivity(true)
        val refreshLayout = refreshRecyclerView.getRefreshLayout()
        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setRefreshListenerHaptic {
            refresh()
        }

        val recyclerView = refreshRecyclerView.getRecyclerView()
        recyclerView.addItemDecoration(LineDivider(requireContext()))
        recyclerView.setBackgroundColor(Color.WHITE)
        adapter.setLoadMoreView(LoadMoreView())
        adapter.disableLoadMoreIfNotFullPage(recyclerView)
        adapter.setEnableLoadMore(true)
        adapter.setOnLoadMoreListener(
            {
                if (!refreshLayout.isRefreshing) {
                    loadMore()
                }
            },
            recyclerView
        )
        adapter.setOnItemClickListener { adapter, _, position ->
            (adapter.getItem(position) as? TimelineAdapter.TimelineEntity)?.let {
                TimelineCommentActivity.launch(requireContext(), it.timeline)
            }
        }
        recyclerView.adapter = adapter
    }


    private fun ensureData() {
        if (data.isEmpty()) {
            Log.d(TAG, "ensureData() -> load api data")
            refresh()
        } else {
            val list = mutableListOf<Timeline>().also { l ->
                data.forEach {
                    l.addAll(it.timelineList)
                }
            }
            adapter.setNewRawData(list)
            showLoadingSuccess(false)
            if (data.lastOrNull()?.next == null) {
                showLoadMoreEnd()
            } else {
                showLoadMoreComplete()
            }
            Log.d(TAG, "ensureData() -> load cache data ${list.map { it.user.nickname }}")
        }
    }

    private fun refresh() {
        Log.d(TAG, "refresh()")
        data.clear()
        loadNewData()
    }

    private fun setData(page: TimelinePage) {
        data.add(page)
        adapter.setNewRawData(page.timelineList)
    }

    private fun addData(page: TimelinePage) {
        data.add(page)
        adapter.addRawData(page.timelineList)
    }

    private fun showLoadMoreEnd() {
        adapter.loadMoreEnd()
    }

    private fun showLoadMoreFailed() {
        adapter.loadMoreFail()
    }

    private fun showLoadMoreComplete() {
        adapter.loadMoreComplete()
    }

    private fun showRefreshing() {
        refreshRecyclerView.showLoading()
    }

    private fun showLoadingError() {
        refreshRecyclerView.showError(R.drawable.ui_ic_oops_network, R.string.ui_oops_net_error)
    }

    private fun showLoadingSuccess(anim: Boolean = true) {
        refreshRecyclerView.showSuccess(anim)
    }

    private fun showLoadingEmpty() {
        refreshRecyclerView.showError(R.drawable.ui_ic_oops_box, R.string.timeline_home_following_empty)
    }

    abstract fun onGetApiSingle(map: Map<String, String?> = mapOf()): Single<TimelinePage>

    private fun loadNewData() {
        Log.d(TAG, "loadData api data")
        showRefreshing()
        onGetApiSingle().subscribeApi(
            this,
            {
                setData(it)
                if (it.timelineList.isEmpty()) {
                    showLoadingEmpty()
                } else {
                    showLoadingSuccess()
                }
                if (it.next == null) {
                    showLoadMoreEnd()
                } else {
                    showLoadMoreComplete()
                }
            },
            {
                Log.e(TAG, "loadData failed", it)
                showLoadingError()
                showLoadMoreFailed()
            }
        )
    }

    private fun loadMore() {
        Log.d(TAG, "loadMore api data")
        onGetApiSingle(data.lastOrNull()?.nextQ ?: mapOf()).subscribeApi(
            this,
            {
                addData(it)
                if (it.next == null) {
                    showLoadMoreEnd()
                } else {
                    showLoadMoreComplete()
                }
            },
            {
                Log.e(TAG, "loadMore failed", it)
                showLoadMoreFailed()
            }
        )
    }

    private fun onMoreClick(entity: TimelineAdapter.TimelineEntity) {
        val options = arrayOf(getString(R.string.timeline_ban_report))
        AlertDialog.Builder(requireContext()).setItems(options) { dialog, which ->
            IRelationshipPlugin.get()
                .launchBanReportTimelineActivity(requireContext(), entity.timeline.user.uuid, entity.timeline.id)
        }.create().show()
    }
}