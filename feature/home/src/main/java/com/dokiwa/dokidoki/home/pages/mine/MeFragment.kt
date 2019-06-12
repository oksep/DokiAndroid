package com.dokiwa.dokidoki.home.pages.mine

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.RelationCount
import com.dokiwa.dokidoki.home.pages.BasePageFragment
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : BasePageFragment(R.layout.fragment_me) {

    private var profile: UserProfile? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entranceUserPage.setOnClickListener {
            profile?.let {
                FeaturePlugin.get(IProfilePlugin::class.java).launchProfileActivity(requireContext(), it.uuid)
            }
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

    override fun onResume() {
        super.onResume()
        // loadData()
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
        val profile = user.profile
        avatar.loadAvatar(user.profile)
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

        this.profile = profile
    }

    private fun setRelationCount(relationCount: RelationCount) {
        fansCountTextView.text = relationCount.followerTotal.toString()
        followCountTextView.text = relationCount.followingTotal.toString()
        uFeedCountTextView.text = "0"
    }
}
