package com.dokiwa.dokidoki.login

import com.dokiwa.dokidoki.center.sp.BaseSharedPreferences
import com.dokiwa.dokidoki.login.api.model.UserToken
import com.google.gson.Gson

/**
 * Created by Septenary on 2018/12/31.
 */
object LoginSP : BaseSharedPreferences("login") {
    const val KEY_USER_TOKEN = "key.user_token"

    fun saveUserToken(userToken: UserToken) {
        save(KEY_USER_TOKEN, Gson().toJson(userToken))
    }

    fun getUserToken(): UserToken? {
        val json = LoginSP.getString(LoginSP.KEY_USER_TOKEN, "")
        return try {
            Gson().fromJson(json, UserToken::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}