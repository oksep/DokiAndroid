package com.dokiwa.dokidoki.profile.home

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.dialog.ShareDialog
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.model.MineProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.certification.CertificationActivity
import com.dokiwa.dokidoki.profile.setting.SettingActivity
import com.dokiwa.dokidoki.social.SocialHelper
import kotlinx.android.synthetic.main.fragment_home_mine.*
import kotlinx.android.synthetic.main.view_mine_counts.*

private const val TAG = "MineFragment"

private const val KEY_VIEW_MODEL = 0x0004

private const val REQUEST_CODE_EDIT_PROFILE = 0x0001

class MineFragment : BaseShareFragment(R.layout.fragment_home_mine) {

    private fun ensureModel(): MineViewModel {
        return (getModel<MineViewModel>(KEY_VIEW_MODEL) ?: MineViewModel()).also {
            putModel(KEY_VIEW_MODEL, it)
        }
    }

    private var mineProfile: MineProfile?
        get() = ensureModel().profileWrap
        set(value) {
            ensureModel().profileWrap = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        entranceUserPage.setOnClickListener {
            mineProfile?.profile?.let {
                IProfilePlugin.get().launchProfileActivity(requireContext(), it)
            }
        }

        entranceEdit.setOnClickListener {
            mineProfile?.profile?.let {
                IProfilePlugin.get().launchEditProfileActivity(this, REQUEST_CODE_EDIT_PROFILE, it)
            }
        }

        entranceBlackList.setOnClickListener {
            IRelationshipPlugin.get().launchBlackListActivity(requireContext())
        }

        entranceFeedback.setOnClickListener {
            IRelationshipPlugin.get().launchFeedbackActivity(requireContext())
        }

        entranceSettings.setOnClickListener {
            SettingActivity.launch(requireContext())
        }

        entranceShare.setOnClickListener {
            showShareDialog()
        }

        timelineCountContainer.setOnClickListener {
            this.mineProfile?.profile?.run {
                ITimelinePlugin.get().launchUserTimelineActivity(requireActivity(), userId.toString(), nickname)
            }
        }

        followCountContainer.setOnClickListener {
            IRelationshipPlugin.get().launchRelationshipActivity(requireContext(), true)
        }

        fansCountContainer.setOnClickListener {
            IRelationshipPlugin.get().launchRelationshipActivity(requireContext(), false)
        }

        certifyContainer.setOnClickListener {
            CertificationActivity.launch(requireContext())
        }

        ensureData()
    }

    private fun ensureData() {
        val mineProfile = mineProfile
        if (mineProfile == null) {
            Log.d(TAG, "ensure profile -> load api data")
            loadProfile()
        } else {
            setProfile(mineProfile)
            Log.d(TAG, "ensure profile -> load cache data $mineProfile")
        }
    }

    private fun loadProfile() {
        Api.get(ProfileApi::class.java)
            .getMeProfile()
            .subscribeApi(this, ::setProfile, ::loadError)
    }

    private fun loadError(error: Throwable) {
        Log.e(TAG, "load data error", error)
    }

    private fun setProfile(mineProfile: MineProfile) {
        val profile = mineProfile.profile
        avatar.glideAvatar(mineProfile.profile)
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
        this.mineProfile = mineProfile

        fansCount.text = mineProfile.relationStats.followerTotal.toString()
        followCount.text = mineProfile.relationStats.followingTotal.toString()
        timelineCount.text = mineProfile.timelineStats.timelineTotal.toString()

        ILoginPlugin.get().updateUserProfile(profile)
    }

    private fun showShareDialog() {
        ShareDialog(requireActivity()) {
            SocialHelper.shareWebPage(
                requireActivity(),
                it,
                getString(R.string.profile_home_mine_share_title),
                getString(R.string.profile_home_mine_share_content),
                getString(R.string.profile_home_mine_share_website),
                (ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.share_logo,
                    null
                ) as BitmapDrawable).bitmap
            ).subscribeApi(this)
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            loadProfile()
        }
    }
}
