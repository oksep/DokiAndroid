package com.dokiwa.dokidoki.login

import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.sp.BaseSharedPreferences
import com.dokiwa.dokidoki.login.model.UserToken
import com.google.gson.Gson

/**
 * Created by Septenary on 2018/12/31.
 */
object LoginSP : BaseSharedPreferences("login", asUserData = false) {

    private const val KEY_USER_TOKEN = "key.login_user_token"
    private const val KEY_USER_PROFILE = "key.login_user_profile"

    fun saveUserToken(userToken: UserToken) {
        save(KEY_USER_TOKEN, Gson().toJson(userToken))
    }

    fun getUserToken(): UserToken? {
        val json = getString(KEY_USER_TOKEN, "")
        return try {
            Gson().fromJson(json, UserToken::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

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

    fun clearLoginUser() {
        clearAll()
    }
}