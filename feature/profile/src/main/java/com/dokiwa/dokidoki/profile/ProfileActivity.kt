package com.dokiwa.dokidoki.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toPrettyJson
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.profile.api.ProfileApi
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : TranslucentActivity() {

    companion object {
        private const val EXTRA_USER_ID = "extra.user_uuid"
        fun launch(context: Context, uuid: String) {
            context.startActivity(
                Intent(context, ProfileActivity::class.java).putExtra(EXTRA_USER_ID, uuid)
            )
        }
    }

    private val userId by lazy { intent.getStringExtra(EXTRA_USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        loadData()
    }

    private fun loadData() {
        Api.get(ProfileApi::class.java)
            .getUserProfileByUUID(userId)
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
