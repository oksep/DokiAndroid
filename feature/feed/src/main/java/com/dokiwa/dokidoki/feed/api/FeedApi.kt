package com.dokiwa.dokidoki.feed.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * Created by Septenary on 2019-06-23.
 */
interface FeedApi {
    @GET("{path}")
    fun getFeedList(
        @Path(value = "path", encoded = true) path: String = "/api/feed/v1/me",
        @QueryMap(encoded = true) map: Map<String, String?> = mapOf()
    ): Single<FeedPage>
}