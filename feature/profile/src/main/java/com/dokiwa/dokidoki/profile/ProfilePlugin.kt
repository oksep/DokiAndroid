package com.dokiwa.dokidoki.profile

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin

/**
 * Created by Septenary on 2019/1/2.
 */
class ProfilePlugin : IProfilePlugin {

    override fun launchProfileActivity(context: Context) {
        ProfileActivity.launch(context)
    }
}