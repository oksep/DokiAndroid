package com.dokiwa.dokidoki.profile.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.profile.ProfileSP
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.edit.ProfileEditActivity
import com.dokiwa.dokidoki.ui.ext.onceLayoutThen
import com.dokiwa.dokidoki.ui.ext.zoomTouchArea
import com.dokiwa.dokidoki.ui.span.HtmlSpan
import com.jaeger.ninegridimageview.NineGridImageView
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.view_profile_detail_bottom.*
import kotlinx.android.synthetic.main.view_profile_detail_certify.*
import kotlinx.android.synthetic.main.view_profile_detail_head.*
import kotlinx.android.synthetic.main.view_profile_detail_pictures.*
import kotlinx.android.synthetic.main.view_profile_detail_pictures.picturesEmpty
import kotlinx.android.synthetic.main.view_profile_detail_timeline.*

class ProfileDetailActivity : TranslucentActivity() {

    companion object {
        private const val EXTRA_UUID = "extra.user.uuid"
        private const val EXTRA_PROFILE = "extra.user.profile"

        fun launch(context: Context, uuid: String) {
            context.startActivity(
                Intent(context, ProfileDetailActivity::class.java).putExtra(EXTRA_UUID, uuid)
            )
        }

        fun launch(context: Context, profile: UserProfile) {
            context.startActivity(
                Intent(context, ProfileDetailActivity::class.java).putExtra(EXTRA_PROFILE, profile)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_profile_detail)
        loadData()
    }

    private fun ensureEditAble(userId: Int): Boolean {
        return ProfileSP.getLoginUserProfile()?.userId == userId
    }

    private fun loadData() {
        val profile: UserProfile? = intent.getParcelableExtra(EXTRA_PROFILE)
        val uuid = profile?.uuid ?: intent.getStringExtra(EXTRA_UUID)

        if (profile != null) {
            setData(UserProfileWrap(profile))
            return
        } else {
            Api.get(ProfileApi::class.java)
                .getUserProfileByUUID(uuid)
                .subscribeApiWithDialog(this, this, ::setData) {
                    toastApiException(it, R.string.center_toast_loading_failed_retry)
                }
        }
    }

    private fun setData(profileWrap: UserProfileWrap) {
        val profile = profileWrap.profile

        name.text = profile.nickname

        avatar.loadAvatar(profile)
        userId.text = getString(R.string.profile_detail_id, profile.userId.toString())
        ageHeightEdu.text = profile.assembleAgeHeightEdu(this)
        addressPosition.text = profile.assembleCityIndustryIncome()
        incomeDogeIcon.visibility = if (profile.income > 0) View.VISIBLE else View.GONE

        if (profile.tags.isNullOrEmpty()) {
            tags.visibility = View.GONE
            tagsEmpty.visibility = View.VISIBLE
        } else {
            tags.visibility = View.VISIBLE
            tagsEmpty.visibility = View.GONE
            tags.setTags(profile.tags!!.map { it.name })
        }

        if (profile.verify != null) {
            officialCertify.visibility = View.VISIBLE
            officialCertify.text = getString(R.string.profile_detail_official_certify, profile.verify!!.title)
            officialCertifyLine.visibility = View.VISIBLE
        } else {
            officialCertify.visibility = View.GONE
            officialCertifyLine.visibility = View.GONE
        }

        identifyCertify.text = if (profile.certification.identification != null) {
            getString(R.string.profile_detail_identify_certified)
        } else {
            HtmlSpan.fromHtml(getString(R.string.profile_detail_identify_certify_none))
        }

        eduCertify.text = if (profile.certification.education != null) {
            getString(R.string.profile_detail_edu_certified)
        } else {
            HtmlSpan.fromHtml(getString(R.string.profile_detail_edu_certify_none))
        }

        if (profile.intro.isNullOrEmpty()) {
            introEmpty.visibility = View.VISIBLE
            introToggle.visibility = View.GONE
            intro.visibility = View.GONE
        } else {
            introEmpty.visibility = View.GONE
            intro.visibility = View.VISIBLE
            intro.setText(profile.intro, introToggle)
            introToggle.visibility = View.VISIBLE
            introToggle.onceLayoutThen {
                it.zoomTouchArea(introToggle.parent as View)
            }
        }

        val nineGridImageView = pictures as NineGridImageView<UserProfile.Picture>

        if (profile.pictures.isNullOrEmpty()) {
            picturesEmpty.visibility = View.VISIBLE
            nineGridImageView.visibility = View.GONE
        } else {
            picturesEmpty.visibility = View.GONE
            nineGridImageView.visibility = View.VISIBLE
            nineGridImageView.setAdapter(picturesAdapter)
            nineGridImageView.setImagesData(profile.pictures)
        }

        timelinePictures.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        timelinePictures.adapter = timelineAdapter.apply {
            setNewData(profile.pictures)
        }

        val editable = ensureEditAble(profile.userId)
        if (editable) {
            toolBar.rightIconView.visibility = View.VISIBLE
            bottomContainer.visibility = View.GONE
            activeTime.visibility = View.GONE
            toolBar.setOnClickListener {
                ProfileEditActivity.launch(this, profile)
            }
        } else {
            toolBar.rightIconView.visibility = View.GONE
            bottomContainer.visibility = View.VISIBLE
            activeTime.visibility = View.VISIBLE
            activeTime.text = profile.lastActive.toReadable()
            sendMsgBtn.setOnClickListener {
                toast("发送消息")
            }
            followBtn.setOnClickListener {
                toast("关注")
            }
        }
    }

    private val timelineAdapter by lazy {
        object : BaseQuickAdapter<UserProfile.Picture, BaseViewHolder>(
            R.layout.view_item_profile_detail_timeline, null
        ) {
            override fun convert(helper: BaseViewHolder?, item: UserProfile.Picture) {
                (helper?.itemView as? ImageView)?.loadImgFromNetWork(item.adaptUrl())
            }
        }
    }

    private val picturesAdapter by lazy {
        object : NineGridImageViewAdapter<UserProfile.Picture>() {
            override fun onDisplayImage(context: Context, imageView: ImageView, item: UserProfile.Picture) {
                imageView.loadImgFromNetWork(item.adaptUrl(), 0, ImageView.ScaleType.CENTER_CROP)
            }

            override fun generateImageView(context: Context): ImageView {
                return View.inflate(context, R.layout.view_item_profile_detail_pictures, null) as ImageView
            }

            override fun onItemImageClick(
                context: Context?,
                imageView: ImageView?,
                index: Int,
                list: List<UserProfile.Picture>?
            ) {
                list?.let { l ->
                    GalleryActivity.launchGallery(this@ProfileDetailActivity, index, l.map { it.adaptUrl() })
                }
            }
        }
    }
}
