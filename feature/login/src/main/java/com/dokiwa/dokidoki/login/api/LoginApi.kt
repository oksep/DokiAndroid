package com.dokiwa.dokidoki.login.api

import com.dokiwa.dokidoki.login.api.model.UserToken
import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Septenary on 2018/12/3.
 */
interface LoginApi {

    @FormUrlEncoded
    @POST("/api/captcha/v1/send")
    fun getVerifyCode(@Field("phone") phoneNumber: String): Single<JsonElement>

    @FormUrlEncoded
    @POST("/api/auth/v1/token")
    fun loginByVerifyCode(
        @Field("x_phone_number") phoneNumber: String,
        @Field("x_phone_code") verifyCode: String,
        @Field("grant_type") grantType: String = "x-phone"
    ): Single<UserToken>

}