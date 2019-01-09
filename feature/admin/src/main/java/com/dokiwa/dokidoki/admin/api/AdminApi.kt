package com.dokiwa.dokidoki.admin.api

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by Septenary on 2019/1/9.
 */
interface AdminApi {

    @GET("/api/profile/v1/me")
    fun getProfile(): Single<JsonElement>

    @GET("/api/doki/v1/city")
    fun getCityConfig(): Single<JsonElement>

    @GET("/api/social/v1/info")
    fun getThirdPartyInfo(): Single<JsonElement>

    @GET("/api/social/v1/list")
    fun getThirdPartyList(): Single<JsonElement>

    @GET("/api/user/v1/phone")
    fun getBindPhone(): Single<JsonElement>

    @GET("/api/notification/v1/me")
    fun getNotification(): Single<JsonElement>

    @GET("/api/setting/v1/me")
    fun getUserConfig(): Single<JsonElement>

}