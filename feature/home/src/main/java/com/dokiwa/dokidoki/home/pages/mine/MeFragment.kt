package com.dokiwa.dokidoki.home.pages.mine

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.home.Log
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.RelationCount
import com.dokiwa.dokidoki.home.pages.BasePageFragment
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.view_me_counts.*

private const val TAG = "MeFragment"

class MeFragment : BasePageFragment(R.layout.fragment_me) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entranceUserPage.setOnClickListener {
            sharedViewModel.mineViewModel.profile?.profile?.let {
                IProfilePlugin.get().launchProfileActivity(requireContext(), it)
            }
        }

        entranceEdit.setOnClickListener {
            val profile = sharedViewModel.mineViewModel.profile?.profile
            if (profile != null) {
                IProfilePlugin.get().launchEditProfileActivity(requireContext(), profile)
            }
        }

        entranceBlackList.setOnClickListener {
            IProfilePlugin.get().launchCreateProfileActivity(requireContext(), null)
        }

        entranceFeedback.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceSettings.setOnClickListener {
            requireContext().toast("TODO")
        }

        entranceShare.setOnClickListener {
            requireContext().toast("TODO")
        }

        timelineCountContainer.setOnClickListener {
            requireContext().toast("TODO")
        }

        followCountContainer.setOnClickListener {
            requireContext().toast("TODO")
        }

        fansCountContainer.setOnClickListener {
            requireContext().toast("TODO")
        }

        ensureData()
    }

    private fun ensureData() {
        val profileWrap = sharedViewModel.mineViewModel.profile
        if (profileWrap == null) {
            Log.d(TAG, "ensure profile -> load api data")
            loadProfile()
        } else {
            setProfile(profileWrap)
            Log.d(TAG, "ensure profile -> load cache data $profileWrap")
        }

        val relationCount = sharedViewModel.mineViewModel.relationCount
        if (relationCount == null) {
            Log.d(TAG, "ensure relation -> load api data")
            loadRelationCount()
        } else {
            setRelationCount(relationCount)
            Log.d(TAG, "ensure relation -> load cache data $relationCount")
        }

        val timelineCount = sharedViewModel.mineViewModel.timeLineCount
        if (timelineCount == null) {
            Log.d(TAG, "ensure timeline -> load api data")
            loadTimeLineCount()
        } else {
            setTimeLineCount(timelineCount)
            Log.d(TAG, "ensure timeline -> load cache data $timelineCount")
        }
    }

    private fun loadProfile() {
        Api.get(HomeApi::class.java)
            .getMeProfile()
            .subscribeApi(this, ::setProfile, ::loadError)
    }

    private fun loadRelationCount() {
        Api.get(HomeApi::class.java)
            .getRelationCount()
            .subscribeApi(this, ::setRelationCount, ::loadError)
    }

    private fun loadTimeLineCount() {
        Api.get(HomeApi::class.java)
            .getTimeLineJson()
            .subscribeApi(this, {
                val count = try {
                    it.asJsonObject.get("total")?.asInt ?: 0
                } catch (e: Exception) {
                    e.printStackTrace()
                    0
                }
                setTimeLineCount(count)
            }, ::loadError)
    }

    private fun loadError(error: Throwable) {
        Log.e(TAG, "load data error", error)
    }

    private fun setProfile(profileWrap: UserProfileWrap) {
        val profile = profileWrap.profile
        avatar.loadAvatar(profileWrap.profile)
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

        sharedViewModel.mineViewModel.profile = profileWrap
    }

    private fun setRelationCount(relationCount: RelationCount) {
        fansCount.text = relationCount.followerTotal.toString()
        followCount.text = relationCount.followingTotal.toString()
        sharedViewModel.mineViewModel.relationCount = relationCount
    }

    private fun setTimeLineCount(count: Int) {
        timelineCount.text = count.toString()
        sharedViewModel.mineViewModel.timeLineCount = count
    }
}
