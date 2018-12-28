package com.dokiwa.dokidoki.login.api

import com.dokiwa.dokidoki.center.api.model.ApiData
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
    fun getVerifyCode(@Field("phone") phone: String): Single<ApiData<Any>>
}