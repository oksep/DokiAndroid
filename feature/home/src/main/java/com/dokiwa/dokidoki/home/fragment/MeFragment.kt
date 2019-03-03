package com.dokiwa.dokidoki.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toPrettyJson
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.RelationCount
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : BaseFragment(), OnPageSelectedListener {

    companion object {
        fun newInstance() = MeFragment()
    }

    private val sharedModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entranceUserPage.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceEdit.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceBlackList.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceFeedback.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceSettings.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceEvaluate.setOnClickListener {
            requireContext().toast("TODO")
        }

        uFeedTitleTextView.setOnClickListener(::toUFeedListPage)
        uFeedCountTextView.setOnClickListener(::toUFeedListPage)

        followCountTextView.setOnClickListener(::toFollowListPage)
        followTitleTextView.setOnClickListener(::toFollowListPage)

        fansTitleTextView.setOnClickListener(::toFansListPage)
        fansCountTextView.setOnClickListener(::toFansListPage)


    }

    override fun onPageSelected() {
        loadData()
    }

    private fun toUFeedListPage(view: View) {
        requireContext().toast("TODO toUFeedListPage")
    }

    private fun toFollowListPage(view: View) {
        requireContext().toast("TODO toFollowListPage")
    }

    private fun toFansListPage(view: View) {
        requireContext().toast("TODO toFansListPage")
    }

    private fun loadData() {
        Api.get(HomeApi::class.java)
            .getMeProfile()
            .subscribeApi(this, ::setProfile)

        Api.get(HomeApi::class.java)
            .getRelatinCount()
            .subscribeApi(this, ::setRelationCount)
    }

    private fun setProfile(user: UserProfileWrap) {
        text.text = user.toPrettyJson()
        val profile = user.profile
//        Glide.with(this).load(profile.avatar.middleUrl ?: profile.avatar.url).into(avatar as ImageView)
        nameTextView.text = profile.nickname
        idTextView.text = getString(R.string.home_me_id, profile.userId)
        if (profile.verify != null) {
            officialCertifyTextView.text = getString(R.string.home_me_official_certify, profile.verify?.title)
            certifyIconView.visibility = View.VISIBLE
            officialCertifyTextView.visibility = View.VISIBLE
        } else {
            certifyIconView.visibility = View.GONE
            officialCertifyTextView.visibility = View.GONE
        }
        realNameCertifyStatusTextView.visibility = if (profile.certification.identification == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
        eduCertifyStatusTextView.visibility = if (profile.certification.identification == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setRelationCount(relationCount: RelationCount) {
        fansCountTextView.text = relationCount.followerTotal.toString()
        followCountTextView.text = relationCount.followingTotal.toString()
        uFeedCountTextView.text = "0"
    }
}
