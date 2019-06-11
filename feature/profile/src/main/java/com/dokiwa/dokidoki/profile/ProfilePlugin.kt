package com.dokiwa.dokidoki.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.profile.create.CreateProfileActivity
import com.dokiwa.dokidoki.profile.detail.ProfileDetailActivity
import com.dokiwa.dokidoki.profile.dialog.CityPickerDialog
import com.dokiwa.dokidoki.profile.search.SearchUserActivity

/**
 * Created by Septenary on 2019/1/2.
 */
class ProfilePlugin : IProfilePlugin {

    override fun launchProfileActivity(context: Context, uuid: String) {
        ProfileDetailActivity.launch(context, uuid)
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

    override fun getCityPickerDialog(context: Activity, callback: (String, String, String) -> Unit): Dialog {
        return CityPickerDialog.create(context, callback)
    }

    override fun launchSearchUserActivity(context: Context) {
        SearchUserActivity.launch(context)
    }
}