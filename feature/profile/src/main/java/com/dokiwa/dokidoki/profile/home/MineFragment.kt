package com.dokiwa.dokidoki.profile.home

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.api.RelationCount
import kotlinx.android.synthetic.main.fragment_home_mine.*
import kotlinx.android.synthetic.main.view_mine_counts.*

private const val TAG = "MineFragment"

private const val KEY_VIEW_MODEL = 0x0004

class MineFragment : BaseShareFragment(R.layout.fragment_home_mine) {

    private fun ensureModel(): MineViewModel {
        return (getModel<MineViewModel>(KEY_VIEW_MODEL) ?: MineViewModel()).also {
            putModel(KEY_VIEW_MODEL, it)
        }
    }

    private var profileWrap: UserProfileWrap?
        get() = ensureModel().profileWrap
        set(value) {
            ensureModel().profileWrap = value
        }

    private val countsModel: CountsViewModel
        get() = ensureModel().countsModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entranceUserPage.setOnClickListener {
            profileWrap?.profile?.let {
                IProfilePlugin.get().launchProfileActivity(requireContext(), it)
            }
        }

        entranceEdit.setOnClickListener {
            profileWrap?.profile?.let {
                IProfilePlugin.get().launchEditProfileActivity(requireContext(), it)
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
            this.profileWrap?.profile?.run {
                ITimelinePlugin.get().launchUserTimelineActivity(requireActivity(), userId.toString(), nickname)
            }
        }

        followCountContainer.setOnClickListener {
            IRelationshipPlugin.get().launchRelationshipActivity(requireContext(), true)
        }

        fansCountContainer.setOnClickListener {
            IRelationshipPlugin.get().launchRelationshipActivity(requireContext(), false)
        }

        ensureData()
    }

    private fun ensureData() {
        val profileWrap = profileWrap
        if (profileWrap == null) {
            Log.d(TAG, "ensure profile -> load api data")
            loadProfile()
        } else {
            setProfile(profileWrap)
            Log.d(TAG, "ensure profile -> load cache data $profileWrap")
        }

        val relationCount = countsModel.relationCount
        if (relationCount == null) {
            Log.d(TAG, "ensure relation -> load api data")
            loadRelationCount()
        } else {
            setRelationCount(relationCount)
            Log.d(TAG, "ensure relation -> load cache data $relationCount")
        }

        val timelineCount = countsModel.timeLineCount
        if (timelineCount == null) {
            Log.d(TAG, "ensure timeline -> load api data")
            loadTimeLineCount()
        } else {
            setTimeLineCount(timelineCount)
            Log.d(TAG, "ensure timeline -> load cache data $timelineCount")
        }
    }

    private fun loadProfile() {
        Api.get(ProfileApi::class.java)
            .getMeProfile()
            .subscribeApi(this, ::setProfile, ::loadError)
    }

    private fun loadRelationCount() {
        Api.get(ProfileApi::class.java)
            .getRelationCount()
            .subscribeApi(this, ::setRelationCount, ::loadError)
    }

    private fun loadTimeLineCount() {
        Api.get(ProfileApi::class.java)
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
        avatar.glideAvatar(profileWrap.profile)
        nameTextView.text = profile.nickname
        idTextView.text = getString(R.string.profile_home_mine_id, profile.userId)
        if (profile.verify != null) {
            officialCertifyTextView.text = getString(R.string.profile_home_mine_official_certify, profile.verify?.title)
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
        this.profileWrap = profileWrap

        ILoginPlugin.get().updateUserProfile(profile)
    }

    private fun setRelationCount(relationCount: RelationCount) {
        fansCount.text = relationCount.followerTotal.toString()
        followCount.text = relationCount.followingTotal.toString()
        countsModel.relationCount = relationCount
    }

    private fun setTimeLineCount(count: Int) {
        timelineCount.text = count.toString()
        countsModel.timeLineCount = count
    }
}
