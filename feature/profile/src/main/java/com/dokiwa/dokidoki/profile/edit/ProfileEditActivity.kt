package com.dokiwa.dokidoki.profile.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.profile.R

class ProfileEditActivity : BaseActivity() {

    companion object {
        private const val EXTRA_PROFILE = "extra.user.profile"

        fun launch(context: Context, profile: UserProfile) {
            context.startActivity(
                Intent(context, ProfileEditActivity::class.java).putExtra(EXTRA_PROFILE, profile)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_profile_edit)
    }
}
