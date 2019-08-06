package com.dokiwa.dokidoki.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.create.CreateProfileActivity
import com.dokiwa.dokidoki.profile.detail.ProfileDetailActivity
import com.dokiwa.dokidoki.profile.dialog.CityPickerDialog
import com.dokiwa.dokidoki.profile.edit.ProfileEditActivity
import com.dokiwa.dokidoki.profile.home.MineFragment
import com.dokiwa.dokidoki.profile.search.SearchUserActivity
import io.reactivex.Single

/**
 * Created by Septenary on 2019/1/2.
 */
class ProfilePlugin : IProfilePlugin {

    override fun obtainHomeMineFragment(): Fragment {
        return MineFragment()
    }

    override fun launchProfileActivity(context: Context, uuid: String) {
        ProfileDetailActivity.launch(context, uuid)
    }

    override fun launchProfileActivity(context: Context, profile: UserProfile) {
        ProfileDetailActivity.launch(context, profile)
    }

    override fun launchCreateProfileActivity(context: Context, userToken: Parcelable?) {
        CreateProfileActivity.launch(context, userToken)
    }

    override fun launchEditProfileActivity(context: Context, profile: UserProfile) {
        ProfileEditActivity.launch(context, profile)
    }

    override fun getCityPickerDialog(context: Activity, callback: (String, String, String) -> Unit): Dialog {
        return CityPickerDialog.create(context, callback)
    }

    override fun launchSearchUserActivity(context: Context) {
        SearchUserActivity.launch(context)
    }

    override fun getUserProfile(uuid: String): Single<UserProfileWrap> {
        return Api.get(ProfileApi::class.java).getUserProfileByUUID(uuid)
    }
}