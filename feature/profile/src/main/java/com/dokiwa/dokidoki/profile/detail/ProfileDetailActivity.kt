package com.dokiwa.dokidoki.profile.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toPrettyJson
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileDetailActivity : BaseActivity() {

    companion object {
        private const val EXTRA_USER_ID = "extra.user_uuid"
        fun launch(context: Context, uuid: String) {
            context.startActivity(
                Intent(context, ProfileDetailActivity::class.java).putExtra(EXTRA_USER_ID, uuid)
            )
        }
    }

    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_profile)
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
        Api.get(ProfileApi::class.java)
            // .getUserProfileByUUID(userId)
            .getUserProfileById("2432")
            .subscribeApi(
                this,
                ::setData
            ) {
                toastApiException(it, R.string.loading_failed_retry)
            }
    }

    private fun setData(user: UserProfileWrap) {
        text.text = user.toPrettyJson()
    }
}
