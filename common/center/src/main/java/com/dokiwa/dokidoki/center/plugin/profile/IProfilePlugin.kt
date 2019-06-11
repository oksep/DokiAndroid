package com.dokiwa.dokidoki.center.plugin.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta
import com.dokiwa.dokidoki.center.plugin.model.UserProfile

/**
 * Created by Septenary on 2019/1/2.
 */
@PluginImplMeta("com.dokiwa.dokidoki.profile.ProfilePlugin")
interface IProfilePlugin : FeaturePlugin {

    fun launchProfileActivity(context: Context, uuid: String)

    fun launchCreateProfileActivity(context: Context, userToken: Parcelable?)

    fun getLoginUserProfile(): UserProfile?

    fun clearUserProfile()

    fun getCityPickerDialog(context: Activity, callback: (String, String, String) -> Unit): Dialog

    fun launchSearchUserActivity(context: Context)

    companion object {
        fun get(): IProfilePlugin {
            return FeaturePlugin.get(IProfilePlugin::class.java)
        }
    }
}