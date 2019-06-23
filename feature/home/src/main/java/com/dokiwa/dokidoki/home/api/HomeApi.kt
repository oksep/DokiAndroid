package com.dokiwa.dokidoki.home.api

import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by Septenary on 2018/12/1.
 */
interface HomeApi {

    @GET("/api/profile/v1/me")
    fun getMeProfile(): Single<UserProfileWrap>
}