package com.dokiwa.dokidoki.center.plugin.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import io.reactivex.Single

/**
 * Created by Septenary on 2019/1/2.
 */
@PluginImplMeta("com.dokiwa.dokidoki.profile.ProfilePlugin")
interface IProfilePlugin : FeaturePlugin {

    fun obtainHomeMineFragment(): Fragment

    fun launchProfileActivity(context: Context, uuid: String)

    fun launchProfileActivity(context: Context, profile: UserProfile)

    fun launchCreateProfileActivity(context: Context, userToken: Parcelable?)

    fun launchEditProfileActivity(context: Context, profile: UserProfile)

    fun getCityPickerDialog(context: Activity, callback: (String, String, String) -> Unit): Dialog

    fun launchSearchUserActivity(context: Context)

    fun getUserProfile(uuid: String): Single<UserProfileWrap>

    fun isNotifyEnable(): Boolean

    fun isSoundEnable(): Boolean

    fun isVibrateEnable(): Boolean

    companion object {
        fun get(): IProfilePlugin {
            return FeaturePlugin.get(IProfilePlugin::class.java)
        }
    }
}