package com.dokiwa.dokidoki.timeline.api

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Septenary on 2019-06-23.
 */
interface TimelineApi {

    @GET("{path}")
    fun getRecommendTimeline(
        @Path(value = "path", encoded = true) path: String = "/api/ufeed/v1/trending",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<TimelinePage>

    @GET("{path}")
    fun getFollowingTimeline(
        @Path(value = "path", encoded = true) path: String = "/api/ufeed/v1/following",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<TimelinePage>

    @GET("{path}")
    fun getUserTimeline(
        @Path(value = "path", encoded = true) path: String = "/api/ufeed/v1/user",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<TimelinePage>

    @GET("/api/ufeed/v1/get")
    fun getTimeline(@Query("ufeed_id") id: String): Single<SingleTimeline>

    @GET("/api/ufeed-comment/v1/list")
    fun getTimelineComment(
        @Query("ufeed_id") id: String,
        @Query("sort") sort: String = "desc",
        @Query("reply_comment_id") replyCommentId: String? = null
    ): Single<TimelineCommentList>

    @FormUrlEncoded
    @POST("/api/ufeed-comment/v1/create")
    fun createComment(
        @Field("ufeed_id") timelineId: String,
        @Field("reply_to_user_id") replyUserId: String? = null,
        @Field("content") content: String
    ): Single<CreateCommentResult>

    @FormUrlEncoded
    @POST("/api/ufeed/v1/create")
    fun createTimeline(
        @Field("pictures") pictures: String? = null, // 图片，可多张，用英文逗号分隔，最多 9 张
        @Field("content") content: String?, // 限制 500 字符
        @Field("position_longitude") positionLongitude: Double? = 0.0, // 经度
        @Field("position_latitude") positionLatitude: Double? = 0.0, // 纬度
        @Field("position_name") positionName: String? = "" // 位置名
    ): Single<JsonElement>

    @GET("/api/notification/v1/me")
    fun getNotification(): Single<TimelineNotification>

    @POST("/api/ufeed/v1/up")
    @FormUrlEncoded
    fun upTimeline(@Field("ufeed_id") id: String): Single<JsonElement>

    @POST("/api/ufeed/v1/cancel-up")
    @FormUrlEncoded
    fun downTimeline(@Field("ufeed_id") id: String): Single<JsonElement>
}