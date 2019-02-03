package com.dokiwa.dokidoki.profile.create.model

import com.contrarywind.interfaces.IPickerViewData
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

data class CityData(
    @SerializedName("province_list")
    val provinceList: List<Province>
) : IApiModel

data class City(
    val code: String,
    val name: String
) : IApiModel, IPickerViewData {
    override fun getPickerViewText() = name
}

data class Province(
    @SerializedName("city_list")
    val cityList: List<City>,
    val code: String,
    val name: String
) : IApiModel, IPickerViewData {
    override fun getPickerViewText() = name
}