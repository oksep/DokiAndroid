package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019-09-08.
 */
data class SettingsModel(
    val setting: Settings
) {
    data class Settings(
        @SerializedName("certificated_only") val certificatedOnly: Boolean,
        @SerializedName("allow_recommend") val allowRecommend: Boolean
    ) : IApiModel
}