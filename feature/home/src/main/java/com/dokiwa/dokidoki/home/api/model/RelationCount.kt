package com.dokiwa.dokidoki.home.api.model

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019/3/3.
 */
class RelationCount(
    @SerializedName("follower_total") val followerTotal: Int,
    @SerializedName("following_total") val followingTotal: Int
) : IApiModel