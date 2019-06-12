package com.dokiwa.dokidoki.profile.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.jaeger.ninegridimageview.NineGridImageView
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.view_profile_detail_timeline.*


class ProfileDetailActivity : BaseActivity() {

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
        tags.setTags(
            listOf(
                "王者荣耀",
                "第五人格",
                "Cosplay",
                "暴走漫画拉拉",
                "声控",
                "烤面筋"
            )
        )
    }

    private fun loadData() {
        val profile: UserProfile? = intent.getParcelableExtra(EXTRA_PROFILE)
        val uuid = profile?.uuid ?: intent.getStringExtra(EXTRA_UUID)

        if (profile != null) {
            setData(UserProfileWrap(profile))
            return
        }

        Api.get(ProfileApi::class.java)
            .getUserProfileByUUID(uuid)
            .subscribeApi(
                this,
                ::setData
            ) {
                toastApiException(it, R.string.loading_failed_retry)
            }
    }

    private fun setData(user: UserProfileWrap) {
        // øtext.text = user.toPrettyJson()
        val nineGridImageView = nineGridImageView as NineGridImageView<String>

        nineGridImageView.setAdapter(mAdapter)
        val list = listOf(
//            "https://pic4.zhimg.com/02685b7a5f2d8cbf74e1fd1ae61d563b_xll.jpg",
//            "https://pic4.zhimg.com/fc04224598878080115ba387846eabc3_xll.jpg",
            "https://pic3.zhimg.com/d1750bd47b514ad62af9497bbe5bb17e_xll.jpg",
            "https://pic4.zhimg.com/da52c865cb6a472c3624a78490d9a3b7_xll.jpg",
            "https://pic3.zhimg.com/0c149770fc2e16f4a89e6fc479272946_xll.jpg",
            "https://pic1.zhimg.com/76903410e4831571e19a10f39717988c_xll.png",
            "https://pic3.zhimg.com/33c6cf59163b3f17ca0c091a5c0d9272_xll.jpg",
            "https://pic4.zhimg.com/52e093cbf96fd0d027136baf9b5cdcb3_xll.png",
            "https://pic3.zhimg.com/f6dc1c1cecd7ba8f4c61c7c31847773e_xll.jpg"
        )
        nineGridImageView.setImagesData(list, NineGridImageView.BOTTOMCOLSPAN)
    }

    private val mAdapter = object : NineGridImageViewAdapter<String>() {
        override fun onDisplayImage(context: Context, imageView: ImageView, s: String) {
            imageView.loadImgFromNetWork(s)
        }

        override fun generateImageView(context: Context): ImageView {
            return super.generateImageView(context)
        }

        override fun onItemImageClick(context: Context?, imageView: ImageView?, index: Int, list: List<String>?) {
            Toast.makeText(context, "image position is $index", Toast.LENGTH_SHORT).show()
        }

        override fun onItemImageLongClick(
            context: Context?,
            imageView: ImageView?,
            index: Int,
            list: List<String>?
        ): Boolean {
            Toast.makeText(context, "image long click position is $index", Toast.LENGTH_SHORT).show()
            return true
        }
    }
}
