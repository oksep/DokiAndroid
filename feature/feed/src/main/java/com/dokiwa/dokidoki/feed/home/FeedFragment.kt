package com.dokiwa.dokidoki.feed.home

import android.graphics.Color
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
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
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
        initToolBar()
        initRefreshRecyclerView()
        ensureData()
    }

    private fun initToolBar() {
        toolBar.rightIconView.setOnClickListener {
            FeedMorePopWindow(
                requireContext(),
                {
                    FeedFilterDialog(requireActivity(), feedFilter) { newFilter ->
                        feedFilter.updateField(newFilter)
                        refresh()
                    }.showAsDropDown(toolBar)
                },
                {
                    IProfilePlugin.get().launchSearchUserActivity(requireContext())
                }
            ).safeShowAsDropDown(it, 0, 0)
        }
    }

    private fun initRefreshRecyclerView() {
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
            (adapter.getItem(position) as? FeedAdapter.FeedEntity)?.let {
                IProfilePlugin.get().launchProfileActivity(requireContext(), it.feed.userProfile)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun ensureData() {
        if (data.isEmpty()) {
            Log.d(TAG, "ensureData() -> load api data")
            refresh()
        } else {
            val list = mutableListOf<Feed>().also { l ->
                data.forEach {
                    l.addAll(it.feedList)
                }
            }
            adapter.setNewRawData(list)
            showLoadingSuccess(false)
            if (data.lastOrNull()?.next == null) {
                showLoadMoreEnd()
            } else {
                showLoadMoreComplete()
            }
            Log.d(TAG, "ensureData() -> load cache data ${list.map { it.userProfile.nickname }}")
        }
    }

    private fun refresh() {
        Log.d(TAG, "refresh()")
        data.clear()
        loadNewData()
    }

    private fun setData(page: FeedPage) {
        data.add(page)
        adapter.setNewRawData(page.feedList)
    }

    private fun addData(page: FeedPage) {
        data.add(page)
        adapter.addRawData(page.feedList)
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

    private fun loadNewData() {
        Log.d(TAG, "loadData api data")
        showRefreshing()
        Api.get(FeedApi::class.java)
            .getFeedList(
                map = feedFilter.asQueryMap()
            )
            .subscribeApi(
                this,
                {
                    setData(it)
                    showLoadingSuccess()
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