package com.dokiwa.dokidoki.home.api

import com.dokiwa.dokidoki.home.api.model2.FeedPage
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

}