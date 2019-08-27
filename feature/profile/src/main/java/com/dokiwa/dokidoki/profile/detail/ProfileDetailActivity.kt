package com.dokiwa.dokidoki.profile.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.glideUrl
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.edit.ProfileEditActivity
import com.dokiwa.dokidoki.ui.ext.onceLayoutThen
import com.dokiwa.dokidoki.ui.ext.zoomTouchArea
import com.dokiwa.dokidoki.ui.span.HtmlSpan
import com.dokiwa.dokidoki.ui.view.DefaultRefreshLayoutHandler
import com.dokiwa.dokidoki.ui.view.IRefreshLayout
import com.jaeger.ninegridimageview.NineGridImageView
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.view_profile_detail_bottom.*
import kotlinx.android.synthetic.main.view_profile_detail_certify.*
import kotlinx.android.synthetic.main.view_profile_detail_head.*
import kotlinx.android.synthetic.main.view_profile_detail_pictures.*
import kotlinx.android.synthetic.main.view_profile_detail_timeline.*

class ProfileDetailActivity : TranslucentActivity(), IRefreshLayout by DefaultRefreshLayoutHandler() {

    companion object {
        private const val EXTRA_UUID = "extra.user.uuid"
        private const val EXTRA_PROFILE = "extra.user.profile"
        private const val TAG = "ProfileDetailActivity"

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
        initRefreshLayout(this, refreshLayout, oopsLayout, contentLayout)
        setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            loadData()
        })
        loadData()
    }

    private fun ensureEditAble(userId: Int): Boolean {
        return ILoginPlugin.get().getLoginUserId() == userId
    }

    private fun loadData() {
        val profile: UserProfile? = intent.getParcelableExtra(EXTRA_PROFILE)
        val uuid = profile?.uuid ?: intent.getStringExtra(EXTRA_UUID)
        val id = intent.data?.getQueryParameter("user_id")

        val single: Single<UserProfile> = if (profile != null) {
            Single.just(profile)
        } else if (!uuid.isNullOrEmpty()) {
            Api.get(ProfileApi::class.java).getUserProfileByUUID(uuid).map { it.profile }
        } else if (!id.isNullOrEmpty()) {
            Api.get(ProfileApi::class.java).getUserProfileById(id).map { it.profile }
        } else {
            Log.e(TAG, "no available arguments.")
            return finish()
        }

        showLoading()

        single.flatMap { pf ->
            ITimelinePlugin.get()
                .getUserTimelineThumbs(pf.userId.toString())
                .map { pf.apply { timelineThumbs = UserProfile.TimelineThumbs(it.first, it.second) } }
        }.subscribeApi(this, {
            showSuccess(true)
            setData(it)
        }, {
            showError(R.drawable.ui_ic_oops_network, R.string.ui_oops_net_error)
        })
    }

    private fun setData(profile: UserProfile) {
        name.text = profile.nickname

        avatar.glideAvatar(profile)
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

        val timelineTotal = profile.timelineThumbs?.total ?: 0
        if (timelineTotal > 0) {
            timelinePicturesEmpty.visibility = View.GONE
            timelineCount.text = getString(R.string.profile_detail_timeline_title, timelineTotal)
            if (profile.timelineThumbs?.thumbList.isNullOrEmpty()) {
                timelinePictures.visibility = View.GONE
            } else {
                timelinePictures.visibility = View.VISIBLE
                timelinePictures.layoutManager = LinearLayoutManager(this).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                timelinePictures.adapter = timelineAdapter.apply {
                    setNewData(profile.timelineThumbs?.thumbList)
                    setOnItemClickListener { _, _, _ ->
                        ITimelinePlugin.get().launchUserTimelineActivity(
                            this@ProfileDetailActivity,
                            profile.userId.toString(),
                            profile.nickname
                        )
                    }
                }
            }
        } else {
            timelineCount.setText(R.string.profile_detail_timeline_title_empty)
            timelinePicturesEmpty.visibility = View.VISIBLE
            timelinePictures.visibility = View.GONE
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
                IMessagePlugin.get().launchChatRoom(this@ProfileDetailActivity, profile)
            }
            followBtn.setRelationsStatus(RelationStatus(profile.userId.toString()))
        }
    }

    private val timelineAdapter by lazy {
        object : BaseQuickAdapter<String, BaseViewHolder>(
            R.layout.view_item_profile_detail_timeline, null
        ) {
            override fun convert(helper: BaseViewHolder?, item: String) {
                (helper?.itemView as? ImageView)?.glideUrl(item, 5f, R.drawable.ui_placeholder_radius_5dp)
            }
        }
    }

    private val picturesAdapter by lazy {
        object : NineGridImageViewAdapter<UserProfile.Picture>() {
            override fun onDisplayImage(context: Context, imageView: ImageView, item: UserProfile.Picture) {
                imageView.glideUrl(item.adaptUrl(), 8f, R.drawable.ui_placeholder_radius_8dp)
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
