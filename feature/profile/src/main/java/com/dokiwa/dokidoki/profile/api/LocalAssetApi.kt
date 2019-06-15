package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.profile.create.model.CityData
import com.dokiwa.dokidoki.profile.create.model.IndustryData
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by Septenary on 2019-06-16.
 */
interface LocalAssetApi {
    @GET("/api/city.json")
    fun getCityListData(): Single<CityData>

    @GET("/api/industry.json")
    fun getIndustryListData(): Single<IndustryData>
}