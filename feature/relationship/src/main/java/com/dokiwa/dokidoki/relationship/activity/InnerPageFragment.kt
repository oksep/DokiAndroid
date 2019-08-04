package com.dokiwa.dokidoki.relationship.activity

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.relationship.Log
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.util.LineDivider
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_relationship_list.*

private const val TAG = "InnerPageFragment"

const val EXTRA_KEY = "extra.view_model.key"

internal abstract class InnerPageFragment : BaseFragment(R.layout.fragment_relationship_list) {

    private val adapter by lazy {
        object : BaseQuickAdapter<Unit, BaseViewHolder>(R.layout.view_item_relationship, null) {
            override fun convert(helper: BaseViewHolder, item: Unit) {
            }
        }
    }

    private val data: MutableList<UserProfile> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefreshRecyclerView()
        loadNewData()
    }

    private fun initRefreshRecyclerView() {
        refreshRecyclerView.enhancedSlidingSensitivity(true)
        val refreshLayout = refreshRecyclerView.getRefreshLayout()
        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setRefreshListenerHaptic {
            refresh()
        }

        val recyclerView = refreshRecyclerView.getRecyclerView()
        // recyclerView.addItemDecoration(LineDivider(requireContext()))
        recyclerView.setBackgroundColor(Color.WHITE)
        adapter.disableLoadMoreIfNotFullPage(recyclerView)
        adapter.setOnItemClickListener { adapter, _, position ->

        }
        recyclerView.adapter = adapter
    }

    private fun refresh() {
        Log.d(TAG, "refresh()")
        data.clear()
        loadNewData()
    }

    private fun setData(page: ) {
        data.add(page)
        adapter.setNewRawData(page.timelineList)
    }

    private fun addData(page: UserProfilePage) {
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
        refreshRecyclerView.showError(
            R.drawable.ui_ic_oops_network,
            R.string.ui_oops_net_error
        )
    }

    private fun showLoadingSuccess(anim: Boolean = true) {
        refreshRecyclerView.showSuccess(anim)
    }

    private fun showLoadingEmpty() {
        refreshRecyclerView.showError(R.drawable.ui_ic_oops_box, R.string.timeline_home_following_empty)
    }

    abstract fun onGetApiSingle(map: Map<String, String?> = mapOf()): Single<UserProfilePage>

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
                Log.e(
                    TAG,
                    "loadData failed",
                    it
                )
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
                Log.e(
                    TAG,
                    "loadMore failed",
                    it
                )
                showLoadMoreFailed()
            }
        )
    }

    private fun onUpClick(view: View, entity: UserProfileAdapter.UserProfileEntity, position: Int) {
        val isUp = entity.timeline.isUp == true
        if (isUp) {
            entity.timeline.upCount -= 1
        } else {
            entity.timeline.upCount += 1
            view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.timeline_heart))
        }
        entity.timeline.isUp = !isUp

        // TODO: 2019-06-27 @Septenary notify not work?
        adapter.notifyItemChanged(position, entity)
    }

    private fun onMoreClick(entity: UserProfileAdapter.UserProfileEntity) {
        AlertDialog.Builder(requireContext()).setMessage("todo").create().show()
    }
}