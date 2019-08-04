package com.dokiwa.dokidoki.relationship.api

import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019-08-04.
 */
data class FollowerWrapList(
    @SerializedName("follower_list") val followerList: List<UserProfile>?
)

data class FollowingWrapList(
    @SerializedName("following_list") val followingList: List<UserProfile>?
)