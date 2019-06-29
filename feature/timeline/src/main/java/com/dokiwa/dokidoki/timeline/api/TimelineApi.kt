package com.dokiwa.dokidoki.timeline.api

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

    @GET("/api/ufeed/v1/get")
    fun getTimeline(@Query("ufeed_id") id: String): Single<Timeline>

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
}