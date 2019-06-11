package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019-06-12.
 */
data class SearchUserResultModel(
    @SerializedName("user_profile_list") val list: List<UserProfile>?
) : IApiModel

