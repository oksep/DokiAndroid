package com.dokiwa.dokidoki.relationship.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.util.LineDivider
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_relationship_list.*
import kotlinx.android.synthetic.main.view_item_relationship.view.*

internal abstract class PageFragment : BaseFragment(R.layout.fragment_relationship_list) {

    private val adapter by lazy {
        object : BaseQuickAdapter<UserProfile, BaseViewHolder>(R.layout.view_item_relationship, null) {
            override fun convert(helper: BaseViewHolder, item: UserProfile) {
                helper.itemView.avatar.glideAvatar(item)
                helper.itemView.name.text = item.nickname
                helper.itemView.intro.text = item.intro
            }
        }
    }

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
        recyclerView.clipToPadding = false
        recyclerView.addItemDecoration(
            LineDivider(
                requireContext().resources,
                R.color.line_light,
                R.dimen.relation_divider_height,
                R.dimen.relation_divider_left,
                R.dimen.relation_divider_right
            )
        )
        recyclerView.setBackgroundColor(Color.WHITE)
        adapter.disableLoadMoreIfNotFullPage(recyclerView)
        adapter.setOnItemClickListener { adapter, _, position ->
            (adapter.getItem(position) as? UserProfile)?.let {
                IProfilePlugin.get().launchProfileActivity(requireContext(), it)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun refresh() {
        loadNewData()
    }

    private fun setData(data: List<UserProfile>) {
        adapter.setNewData(data)
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
        refreshRecyclerView.showError(R.drawable.ui_ic_oops_box, onGetEmptyTip())
    }

    @StringRes
    abstract fun onGetEmptyTip(): Int

    abstract fun onGetApiSingle(): Single<List<UserProfile>>

    private fun loadNewData() {
        showRefreshing()
        onGetApiSingle().subscribeApi(
            this,
            {
                setData(it)
                if (it.isEmpty()) {
                    showLoadingEmpty()
                } else {
                    showLoadingSuccess()
                }
            },
            {
                showLoadingError()
            }
        )
    }
}