package com.dokiwa.dokidoki.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.Education
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.util.birthDayToAge
import com.dokiwa.dokidoki.home.Log
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.Feed
import com.dokiwa.dokidoki.home.api.model.FeedPage
import com.dokiwa.dokidoki.home.dialog.FeedFilterSearchDialog
import com.dokiwa.dokidoki.home.dialog.FeedFilterSearchPopWindow
import com.dokiwa.dokidoki.home.widget.FeedMorePopWindow
import com.dokiwa.dokidoki.home.widget.FeedPictureListView
import com.dokiwa.dokidoki.ui.util.LineDivider
import com.dokiwa.dokidoki.ui.util.safeShowAsDropDown
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import com.dokiwa.dokidoki.ui.view.RoundImageView
import com.dokiwa.dokidoki.ui.view.TagsView
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : BaseFragment(), OnPageSelectedListener {

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

        recyclerView.addItemDecoration(
            LineDivider(
                resources,
                R.color.dd_window,
                R.dimen.home_feed_divider,
                R.dimen.home_feed_divider_mrg,
                R.dimen.home_feed_divider_mrg
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
                IProfilePlugin.get().launchProfileActivity(requireContext(), it.userProfile.uuid)
            }
        }

        recyclerView.adapter = adapter

        refreshLayout.setColorSchemeResources(R.color.dd_red)
        refreshLayout.setOnRefreshListener {
            data.clear()
            loadData()
        }

        toolBar.rightIconView.setOnClickListener {
            FeedMorePopWindow(
                requireContext(),
                {
                    showFilterSearchDialog()
                },
                {
                    requireContext().toast("to search page")
                }
            ).safeShowAsDropDown(it, 0, 0)
        }

        loadData()
    }

    private fun showFilterSearchDialog() {
        // FeedFilterSearchDialog(requireContext()).show()
        FeedFilterSearchPopWindow(requireActivity()).showAsDropDown(toolBar)
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

    override fun requsetScrollContentToTop() {
        recyclerView?.scrollToPosition(0)
    }

    private fun loadData() {
        Api.get(HomeApi::class.java)
            .getFeedList()
            .doOnSubscribe {
                showRefreshing()
            }
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

private class FeedAdapter : BaseQuickAdapter<Feed, BaseViewHolder>(R.layout.view_item_feed, null) {

    override fun convert(helper: BaseViewHolder, item: Feed) {
        val profile = item.userProfile

        // 官方认证
        helper.getView<ImageView>(R.id.officialVerify).visibility =
            if (profile.verify != null) View.VISIBLE else View.GONE

        // 实名认证
        helper.getView<ImageView>(R.id.certifyRealName).visibility =
            if (profile.certification.identification != null) View.VISIBLE else View.GONE

        // 学历认证
        helper.getView<ImageView>(R.id.certifyAcademic).visibility =
            if (profile.certification.education != null) View.VISIBLE else View.GONE

        // 昵称
        helper.getView<TextView>(R.id.name).text = profile.nickname

        // 年龄 | 身高 | 教育程度
        helper.getView<TextView>(R.id.ageHeightEdu).text = assembleAgeHeightEdu(profile)

        // 地点 | 职位
        helper.getView<TextView>(R.id.addressPosition).text = assembleAddressPosition(profile)

        // 头像
        helper.getView<RoundImageView>(R.id.avatar).loadImgFromNetWork(
            profile.avatar.url,
            if (profile.gender == Gender.MALE) {
                R.drawable.ui_ic_avatar_default_male
            } else {
                R.drawable.ui_ic_avatar_default_female
            }
        )

        // 个性标签
        val tagsView = helper.getView<TagsView>(R.id.tags)
        val emptyTagsTip = helper.getView<View>(R.id.emptyTagsTip)
        if (profile.tags.isNullOrEmpty()) {
            tagsView.visibility = View.GONE
            emptyTagsTip.visibility = View.VISIBLE
        } else {
            tagsView.setTags(profile.tags!!.map { it.name })
            tagsView.visibility = View.VISIBLE
            emptyTagsTip.visibility = View.GONE
        }

        // 简介
        val introGroup = helper.getView<Group>(R.id.introGroup)
        if (profile.intro.isNullOrEmpty()) {
            introGroup.visibility = View.GONE
        } else {
            introGroup.visibility = View.VISIBLE
            helper.getView<TextView>(R.id.intro).text = profile.intro
        }

        // pictures
        helper.getView<FeedPictureListView>(R.id.pictureListView).setPictureList(profile.pictures)
    }

    private fun assembleAgeHeightEdu(profile: UserProfile): String {
        val age = profile.birthday.birthDayToAge()
        val edu = profile.education.educationToString()
        val height = if (profile.height > 9) " | ${profile.height}cm" else ""
        val edus = if (edu.isNullOrEmpty()) "" else " | $edu"
        return "${age}岁$height$edus"
    }

    private fun assembleAddressPosition(profile: UserProfile): String {
        val idsty = profile.industry?.name
        return profile.city.name + if (idsty.isNullOrEmpty()) "" else " | $idsty"
    }

    private fun Int.educationToString(): String? {
        return when (this) {
            Education.JUNIOR -> "大专"
            Education.BACHELOR -> "本科"
            Education.MASTER -> "硕士"
            Education.PHD -> "博士"
            else -> ""
        }
    }
}