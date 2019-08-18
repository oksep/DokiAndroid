package com.dokiwa.dokidoki.relationship.api

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Septenary on 2019-08-02.
 */
interface RelationApi {

    @GET("/api/relation/v1/following-list")
    fun getFollowingList(): Single<FollowingWrapList>

    @GET("/api/relation/v1/follower-list")
    fun getFollowerList(): Single<FollowerWrapList>

    @GET("/api/relation/v1/status-list")
    fun geRelationStateList(): Single<JsonElement>

    @GET("{path}")
    fun getBlackList(
        @Path(value = "path", encoded = true) path: String = "/api/blacklist/v1/me",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<BlackListPage>

    @FormUrlEncoded
    @POST("/api/blacklist/v1/del")
    fun delFromBlackList(@Field("user_id") userId: String): Single<JsonElement>

    @FormUrlEncoded
    @POST("/api/blacklist/v1/add")
    fun addToBlackList(@Field("user_id") userId: String): Single<JsonElement>

    @FormUrlEncoded
    @POST("/api/blacklist/v1/state")
    fun isInBlackList(@Field("user_id") userId: String): Single<JsonElement>

    @FormUrlEncoded
    @POST("/api/doki/v1/feedback")
    fun feedback(@Field("content") content: String): Single<JsonElement>

    @GET("/api/report/v1/type-list")
    fun getReportTypeList(): Single<ReportTypeList>

    @GET("/api/reportType.json")
    fun getLocalReportTypeList(): Single<ReportTypeList>
}