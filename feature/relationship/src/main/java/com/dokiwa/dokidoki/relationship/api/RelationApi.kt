package com.dokiwa.dokidoki.relationship.api

import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET

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
}