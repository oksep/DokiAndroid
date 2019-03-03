package com.dokiwa.dokidoki.profile

import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.sp.BaseSharedPreferences
import com.google.gson.Gson

/**
 * Created by Septenary on 2018/12/31.
 */
object ProfileSP : BaseSharedPreferences("login") {

    private const val KEY_USER_PROFILE = "key.user_profile"

    fun saveUserProfile(userToken: UserProfile) {
        save(KEY_USER_PROFILE, Gson().toJson(userToken))
    }

    fun getUserProfile(): UserProfile? {
        val json = getString(KEY_USER_PROFILE, "")
        return try {
            Gson().fromJson(json, UserProfile::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun clearUserProfile() {
        remove(KEY_USER_PROFILE)
    }
}