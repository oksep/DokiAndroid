package com.dokiwa.dokidoki.relationship.blacklist

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
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.relationship.Log
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.relationship.api.BlackListPage
import com.dokiwa.dokidoki.relationship.api.RelationApi
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import kotlinx.android.synthetic.main.activity_blacklist.*
import kotlinx.android.synthetic.main.view_item_blacklist.view.*

private const val TAG = "BlackListActivity"

class BlackListActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, BlackListActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blacklist)

        initRefreshRecyclerView()
        ensureData()
    }


    private val adapter by lazy {
        object : BaseQuickAdapter<UserProfile, BaseViewHolder>(R.layout.view_item_blacklist, null) {
            override fun convert(helper: BaseViewHolder, item: UserProfile) {
                helper.itemView.avatar.glideAvatar(item)
                helper.itemView.name.text = item.nickname
            }
        }
    }

    private val data: MutableList<BlackListPage> = mutableListOf()

    private fun initRefreshRecyclerView() {
        refreshRecyclerView.enhancedSlidingSensitivity(true)
        val refreshLayout = refreshRecyclerView.getRefreshLayout()
        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setRefreshListenerHaptic {
            refresh()
        }

        val recyclerView = refreshRecyclerView.getRecyclerView()
        recyclerView.setBackgroundColor(Color.WHITE)
        adapter.setLoadMoreView(LoadMoreView())
        adapter.disableLoadMoreIfNotFullPage(recyclerView)
        adapter.setOnLoadMoreListener(
            {
                if (!refreshLayout.isRefreshing) {
                    loadMore()
                }
            },
            recyclerView
        )
        adapter.setOnItemClickListener { adapter, _, position ->
            (adapter.getItem(position) as? UserProfile)?.let {
                IProfilePlugin.get().launchProfileActivity(this, it)
            }
        }
        recyclerView.adapter = adapter
    }


    private fun ensureData() {
        if (data.isEmpty()) {
            Log.d(TAG, "ensureData() -> load api data")
            refresh()
        } else {
            val list = mutableListOf<UserProfile>().also { l ->
                data.forEach {
                    l.addAll(it.userList)
                }
            }
            adapter.setNewData(list)
            showLoadingSuccess(false)
            if (data.lastOrNull()?.next == null) {
                showLoadMoreEnd()
            } else {
                showLoadMoreComplete()
            }
            Log.d(TAG, "ensureData() -> load cache data ${list.map { it.nickname }}")
        }
    }

    private fun refresh() {
        Log.d(TAG, "refresh()")
        data.clear()
        loadNewData()
    }

    private fun setData(page: BlackListPage) {
        data.add(page)
        adapter.setNewData(page.userList)
    }

    private fun addData(page: BlackListPage) {
        data.add(page)
        adapter.addData(page.userList)
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
        refreshRecyclerView.showError(R.drawable.ui_ic_oops_box, R.string.relation_black_list_empty)
    }

    private fun loadNewData() {
        Log.d(TAG, "loadData api data")
        showRefreshing()
        Api.get(RelationApi::class.java).getBlackList().subscribeApi(
            this,
            {
                setData(it)
                if (it.userList.isEmpty()) {
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
        Api.get(RelationApi::class.java).getBlackList(
            map = data.lastOrNull()?.nextQ ?: mapOf()
        ).subscribeApi(
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
