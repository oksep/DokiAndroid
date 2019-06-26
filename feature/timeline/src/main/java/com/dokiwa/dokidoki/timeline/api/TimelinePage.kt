package com.dokiwa.dokidoki.timeline.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.api.model.IApiModelPage
import com.dokiwa.dokidoki.center.ext.toRetrofitQueryMap
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.google.gson.annotations.SerializedName

data class TimelinePage(
    @SerializedName("ufeed_list")
    val timelineList: List<Timeline>,
    val next: String?
) : IApiModel, IApiModelPage {
    override val nextQ: Map<String, String?>
        get() = this.next?.toRetrofitQueryMap() ?: mapOf()
}

data class Timeline(
    val id: Int,
    val content: String? = null,
    @SerializedName("comment_count") val commentCount: Int = 0,
    @SerializedName("create_time") val createTime: Int = 0,
    @SerializedName("picture_list") val pictureList: List<TimelinePicture>? = null,
    @SerializedName("up_count") val upCount: Int = 0,
    @SerializedName("is_up") val isUp: Boolean? = false,
    val position: Position? = null,
    val user: TimelineUser
) : IApiModel

data class TimelineUser(
    val id: Int,
    val uuid: String,
    val nickname: String,
    val avatar: UserProfile.Avatar
) : IApiModel

data class TimelinePicture(
    val url: String? = null,
    @SerializedName("middle_url") val middleUrl: String? = null,
    @SerializedName("raw_url") val rawUrl: String,
    val width: Int = 0,
    val height: Int = 0,
    @SerializedName("average_color") val averageColor: Long = 0
) : IApiModel {
    fun adaptUrl(): String {
        return (middleUrl ?: url) ?: rawUrl
    }
}

data class Position(
    val latitude: String?,
    val longitude: String?,
    val name: String?
) : IApiModel