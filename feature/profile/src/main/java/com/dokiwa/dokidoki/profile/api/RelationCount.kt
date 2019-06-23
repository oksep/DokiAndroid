package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019/3/3.
 */
data class RelationCount(
    @SerializedName("follower_total") val followerTotal: Int,
    @SerializedName("following_total") val followingTotal: Int
) : IApiModel