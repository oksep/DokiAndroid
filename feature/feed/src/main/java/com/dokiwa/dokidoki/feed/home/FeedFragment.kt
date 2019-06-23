package com.dokiwa.dokidoki.feed.home

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.feed.Log
import com.dokiwa.dokidoki.feed.R
import com.dokiwa.dokidoki.feed.api.Feed
import com.dokiwa.dokidoki.feed.api.FeedApi
import com.dokiwa.dokidoki.feed.api.FeedPage
import com.dokiwa.dokidoki.feed.widget.FeedMorePopWindow
import com.dokiwa.dokidoki.ui.util.LineDivider
import com.dokiwa.dokidoki.ui.util.safeShowAsDropDown
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import kotlinx.android.synthetic.main.fragment_feed.*

private const val TAG = "FeedFragment"

private const val KEY_VIEW_MODEL = 0x0001

class FeedFragment : BaseShareFragment(R.layout.fragment_feed) {

    private val adapter by lazy { FeedAdapter() }

    private fun ensureModel(): FeedViewModel {
        return (getModel<FeedViewModel>(KEY_VIEW_MODEL) ?: FeedViewModel()).also {
            putModel(KEY_VIEW_MODEL, it)
        }
    }

    private val feedFilter: FeedFilter
        get() = ensureModel().feedFilter

    private val data: MutableList<FeedPage>
        get() = ensureModel().feedPages

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.addItemDecoration(
            LineDivider(
                resources,
                R.color.dd_window,
                R.dimen.feed_divider,
                R.dimen.feed_divider_margin,
                R.dimen.feed_divider_margin
            )
        )
        adapter.setLoadMoreView(LoadMoreView())
        adapter.setOnLoadMoreListener({
            loadMore()
        }, recyclerView)
        adapter.disableLoadMoreIfNotFullPage(recyclerView)
        adapter.setEnableLoadMore(true)
        adapter.setOnItemClickListener { adapter, view, position ->
            (adapter.getItem(position) as? Feed)?.let {
                IProfilePlugin.get().launchProfileActivity(requireContext(), it.userProfile)
            }
        }

        recyclerView.adapter = adapter

        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setOnRefreshListener {
            refresh()
        }

        toolBar.rightIconView.setOnClickListener {
            FeedMorePopWindow(
                requireContext(),
                {
                    showFilterSearchDialog()
                },
                {
                    IProfilePlugin.get().launchSearchUserActivity(requireContext())
                }
            ).safeShowAsDropDown(it, 0, 0)
        }

        ensureData()
    }

    private fun ensureData() {
        if (data.isEmpty()) {
            Log.d(TAG, "ensureData() -> load api data")
            loadData()
        } else {
            val list = mutableListOf<Feed>().also { l ->
                data.forEach {
                    l.addAll(it.feedList)
                }
            }
            adapter.setNewData(list)
            Log.d(
                TAG,
                "ensureData() -> load cache data ${list.map { it.userProfile.nickname }}"
            )
        }
    }

    private fun refresh() {
        Log.d(TAG, "refresh()")
        data.clear()
        loadData()
    }

    private fun showFilterSearchDialog() {
        FeedFilterDialog(requireActivity(), feedFilter) {
            feedFilter.updateField(it)
            refresh()
        }.showAsDropDown(toolBar)
    }

    private fun setData(feedPage: FeedPage) {
        data.add(feedPage)
        adapter.setNewData(feedPage.feedList)
    }

    private fun addData(feedPage: FeedPage) {
        data.add(feedPage)
        adapter.addData(feedPage.feedList)
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
        refreshLayout.isRefreshing = true
    }

    private fun hideRefreshing() {
        refreshLayout.isRefreshing = false
    }

    private fun loadData() {
        Log.d(TAG, "loadData api data")
        showRefreshing()
        Api.get(FeedApi::class.java)
            .getFeedList(
                map = feedFilter.asQueryMap()
            )
            .subscribeApi(
                this,
                {
                    hideRefreshing()
                    setData(it)
                    if (it.next == null) {
                        showLoadMoreEnd()
                    }
                },
                {
                    Log.e(TAG, "loadData failed", it)
                    hideRefreshing()
                }
            )
    }

    private fun loadMore() {
        Log.d(TAG, "loadMore api data")
        Api.get(FeedApi::class.java)
            .getFeedList(
                map = feedFilter.asQueryMap().also {
                    it.putAll(data.lastOrNull()?.nextQ ?: mapOf())
                }
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