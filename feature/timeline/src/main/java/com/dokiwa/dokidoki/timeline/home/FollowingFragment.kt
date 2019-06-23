package com.dokiwa.dokidoki.timeline.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.timeline.api.TimelinePage
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import kotlinx.android.synthetic.main.fragment_timeline_recommend.*

private const val TAG = "FollowingFragment"

private const val KEY_VIEW_MODEL = 0x0006

internal class FollowingFragment : BaseShareFragment(R.layout.fragment_timeline_follow) {

    private val adapter by lazy { TimelineAdapter() }

    private fun ensureModel(): TimelineViewModel {
        return (getModel<TimelineViewModel>(KEY_VIEW_MODEL) ?: TimelineViewModel()).also {
            putModel(KEY_VIEW_MODEL, it)
        }
    }

    private val data: MutableList<TimelinePage>
        get() = ensureModel().timelinePages

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshRecyclerView()
        ensureData()
    }

    private fun initRefreshRecyclerView() {
        val refreshLayout = refreshRecyclerView.getRefreshLayout()
        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setOnRefreshListener {
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
            (adapter.getItem(position) as? Timeline)?.let {
                // TODO: 2019-06-23 @Septenary to detail page
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
            adapter.setNewData(list)
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
        adapter.setNewData(page.timelineList)
    }

    private fun addData(page: TimelinePage) {
        data.add(page)
        adapter.addData(page.timelineList)
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

    private fun showLoadingEmpty() {
        refreshRecyclerView.showError(R.drawable.ui_ic_oops_paper, R.string.timeline_home_following_empty)
    }

    private fun showLoadingSuccess(anim: Boolean = true) {
        refreshRecyclerView.showSuccess(anim)
    }

    private fun loadNewData() {
        Log.d(TAG, "loadData api data")
        showRefreshing()
        Api.get(TimelineApi::class.java)
            .getFollowingTimeline()
            .subscribeApi(
                this,
                {
                    if (it.timelineList.isEmpty()) {
                        setData(it)
                        showLoadingSuccess()
                        if (it.next == null) {
                            showLoadMoreEnd()
                        } else {
                            showLoadMoreComplete()
                        }
                    } else {
//                        showLoadingEmpty()
//                        showLoadMoreFailed()
                        showLoadingError()
                        showLoadMoreFailed()
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
        Api.get(TimelineApi::class.java)
            .getFollowingTimeline(
                map = data.lastOrNull()?.nextQ ?: mapOf()
            )
            .subscribeApi(
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
}