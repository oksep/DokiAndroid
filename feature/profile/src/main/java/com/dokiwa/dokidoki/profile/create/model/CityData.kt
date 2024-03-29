package com.dokiwa.dokidoki.profile.create.model

import com.contrarywind.interfaces.IPickerViewData
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

data class IndustryData(
    @SerializedName("industry_list")
    val industryList: List<Industry>
) : IApiModel

data class SubIndustry(
    val id: Int,
    val name: String
) : IApiModel, IPickerViewData {
    override fun getPickerViewText() = name
}

data class Industry(
    @SerializedName("sub_industry_list")
    val subList: List<SubIndustry>,
    val id: Int,
    val name: String
) : IApiModel, IPickerViewData {
    override fun getPickerViewText() = name
}