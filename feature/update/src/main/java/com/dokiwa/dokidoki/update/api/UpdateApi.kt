package com.dokiwa.dokidoki.update.api

import io.reactivex.Single
import retrofit2.http.GET

interface UpdateApi {

    @GET("/ryfthink/doki-release-android/raw/master/version.json")
    fun checkAppUpdate(): Single<Version>
}