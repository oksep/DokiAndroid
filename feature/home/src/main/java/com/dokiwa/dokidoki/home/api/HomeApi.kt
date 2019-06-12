package com.dokiwa.dokidoki.home.api

import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.home.api.model.FeedPage
import com.dokiwa.dokidoki.home.api.model.RelationCount
import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Created by Septenary on 2018/12/1.
 */
interface HomeApi {

    @GET("{path}")
    fun getFeedList(
        @Path(value = "path", encoded = true) path: String = "/api/feed/v1/me",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<FeedPage>

    @GET("/api/profile/v1/me")
    fun getMeProfile(): Single<UserProfileWrap>

    @GET("/api/relation/v1/count")
    fun getRelationCount(): Single<RelationCount>

    @GET("/api/ufeed/v1/me")
    fun getTimeLineJson(): Single<JsonElement>
}