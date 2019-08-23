package com.dokiwa.dokidoki.relationship.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.api.model.IApiModelPage
import com.dokiwa.dokidoki.center.ext.toRetrofitQueryMap
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2019-08-04.
 */

data class RelationStatus(
    val userId: Int,
    val following: Boolean,
    val follower: Boolean
) : IApiModel

data class RelationStatusList(
    @SerializedName("status_list") val list: List<RelationStatus>?
) : IApiModel

data class RelationStatusWrap(
    val status: RelationStatus
) : IApiModel

data class FollowerWrapList(
    @SerializedName("follower_list") val followerList: List<UserProfile>?
) : IApiModel

data class FollowingWrapList(
    @SerializedName("following_list") val followingList: List<UserProfile>?
) : IApiModel

data class BlackListPage(
    @SerializedName("user_profile_list")
    val userList: List<UserProfile>,
    val next: String?
) : IApiModel, IApiModelPage {
    override val nextQ: Map<String, String?>
        get() = this.next?.toRetrofitQueryMap() ?: mapOf()
}

data class ReportTypeList(
    @SerializedName("type_list") val typeList: List<ReportType>
) : IApiModel {
    data class ReportType(val type: Int, val text: String) : IApiModel
}