package com.dokiwa.dokidoki.profile

import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.profile.create.CreateProfileActivity

/**
 * Created by Septenary on 2019/1/2.
 */
class ProfilePlugin : IProfilePlugin {

    override fun launchProfileActivity(context: Context, uuid: String) {
        ProfileActivity.launch(context, uuid)
    }

    override fun launchCreateProfileActivity(context: Context, userToken: Parcelable?) {
        CreateProfileActivity.launch(context, userToken)
    }

    override fun getLoginUserProfile(): UserProfile? {
        return ProfileSP.getUserProfile()
    }

    override fun clearUserProfile() {
        ProfileSP.clearUserProfile()
    }
}