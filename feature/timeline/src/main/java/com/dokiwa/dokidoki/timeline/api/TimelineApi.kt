package com.dokiwa.dokidoki.timeline.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Created by Septenary on 2019-06-23.
 */
interface TimelineApi {

    @GET("{path}")
    fun getRecommendTimeline(
        @Path(value = "path", encoded = true) path: String = "/api/ufeed/v1/trending",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<TimelinePage>

    @GET("112{path}")
    fun getFollowingTimeline(
        @Path(value = "path", encoded = true) path: String = "/api/ufeed/v1/following",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<TimelinePage>
}