package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019-06-16.
 */
data class TagsGroupModel(
    val name: String,
    @SerializedName("keyword_group_list") val list: List<Tags>?
) : IApiModel {
    data class Tags(
        @SerializedName("keyword_list") val list: List<Tag>?,
        val name: String
    ) : IApiModel

    data class Tag(val name: String) : IApiModel
}
