package com.dokiwa.dokidoki.relationship.api

import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Septenary on 2019-08-02.
 */
interface RelationApi {

    @GET("/api/relation/v1/status-list")
    fun getRelationStatusList(@Query("user_ids") ids: String): Single<RelationStatusList>

    @POST("/api/relation/v1/follow")
    @FormUrlEncoded
    fun followUser(@Field("user_id") userId: String): Single<RelationStatusWrap>

    @POST("/api/relation/v1/unfollow")
    @FormUrlEncoded
    fun unFollowUser(@Field("user_id") userId: String): Single<RelationStatusWrap>

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

fun <T> Single<List<T>>.toRelationStatusPair(getId: (T) -> String): Single<List<Pair<T, RelationStatus>>> {
    return this.flatMap { reqL ->
        Single.create<List<Pair<T, RelationStatus>>> { emitter ->
            val ids = reqL.joinToString(",") { getId(it) }
            Api.get(RelationApi::class.java).getRelationStatusList(ids)
                .onErrorReturn { RelationStatusList(null) }
                .subscribe { statusList ->
                    val result = reqL.map { user ->
                        val id = getId(user)
                        Pair(
                            user,
                            statusList.list?.firstOrNull { getId(user) == it.userId } ?: RelationStatus(id)
                        )
                    }
                    emitter.onSuccess(result)
                }
        }
    }
}