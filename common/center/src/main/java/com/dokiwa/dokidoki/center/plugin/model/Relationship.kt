package com.dokiwa.dokidoki.center.plugin.model

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

data class RelationStatus(
    @SerializedName("user_id") val userId: Int,
    var following: Boolean,
    var follower: Boolean
) : IApiModel