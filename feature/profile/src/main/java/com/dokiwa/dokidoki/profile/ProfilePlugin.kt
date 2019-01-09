package com.dokiwa.dokidoki.profile

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.profile.create.CreateProfileActivity

/**
 * Created by Septenary on 2019/1/2.
 */
class ProfilePlugin : IProfilePlugin {

    override fun launchProfileActivity(context: Context) {
        ProfileActivity.launch(context)
    }

    override fun launchCreateProfileActivity(context: Context) {
        CreateProfileActivity.launch(context)
    }
}