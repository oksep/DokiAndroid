package com.dokiwa.dokidoki.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.home.Log
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.Feed
import com.dokiwa.dokidoki.home.api.model.FeedPage
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment(), OnPageSelectedListener {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private val sharedModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    private var data = mutableListOf<FeedPage>()

    private val adapter by lazy { FeedAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setLoadMoreView(LoadMoreView())
        adapter.setOnLoadMoreListener({
            loadMore()
        }, recyclerView)
        adapter.disableLoadMoreIfNotFullPage(recyclerView)
        adapter.setEnableLoadMore(true)

        recyclerView.adapter = adapter

        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setOnRefreshListener {
            data.clear()
            loadData()
        }

        loadData()
    }

    private fun setData(feedPage: FeedPage) {
        this.data.add(feedPage)
        adapter.setNewData(feedPage.feedList)
    }

    private fun addData(feedPage: FeedPage) {
        this.data.add(feedPage)
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

    override fun onPageSelected() {
        Log.d("AAAAAA", "FeedFragment onPageSelected")
    }

    private fun loadData() {
        Api.get(HomeApi::class.java)
            .getFeedList()
            .doOnSubscribe {
                showRefreshing()
            }
            .subscribeApi(
                context as? CompositeDisposableContext,
                {
                    hideRefreshing()
                    setData(it)
                    if (it.next == null) {
                        showLoadMoreEnd()
                    }
                },
                {
                    hideRefreshing()
                }
            )
    }

    private fun loadMore() {
        Api.get(HomeApi::class.java)
            .getFeedList(map = this.data.lastOrNull()?.nextQ ?: mapOf())
            .subscribeApi(
                context as? CompositeDisposableContext,
                {
                    addData(it)
                    if (it.next == null) {
                        showLoadMoreEnd()
                    } else {
                        showLoadMoreComplete()
                    }
                },
                {
                    showLoadMoreFailed()
                }
            )
    }
}

private class FeedAdapter : BaseQuickAdapter<Feed, BaseViewHolder>(R.layout.item_feed, null) {

    override fun convert(helper: BaseViewHolder, item: Feed) {
        helper.getView<TextView>(R.id.title).text = item.user_profile.nickname
        helper.getView<TextView>(R.id.subTitle).text = item.user_profile.intro
    }
}