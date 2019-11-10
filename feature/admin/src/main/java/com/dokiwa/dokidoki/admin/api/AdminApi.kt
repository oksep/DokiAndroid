package com.dokiwa.dokidoki.admin.api

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Septenary on 2019/1/9.
 */
interface AdminApi {

    @GET("/api/profile/v1/me")
    fun getProfile(): Single<JsonElement>

    @GET("/api/doki/v1/city")
    fun getCityConfig(): Single<JsonElement>

    @GET("/api/doki/v1/industry")
    fun getIndustryConfig(): Single<JsonElement>

    @GET("/api/doki/v1/keyword-group")
    fun getTagsConfig(): Single<JsonElement>

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

    @GET("/api/ufeed/v1/get")
    fun getTimeline(@Query("ufeed_id") id: String): Single<JsonElement>

    @GET("/api/ufeed-comment/v1/list")
    fun getTimelineComment(
        @Query("ufeed_id") id: String,
        @Query("sort") sort: String = "desc",
        @Query("reply_comment_id") replyCommentId: String? = null
    ): Single<JsonElement>
}