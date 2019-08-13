package com.dokiwa.dokidoki.timeline.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.google.gson.annotations.SerializedName

data class TimelineNotification(
    @SerializedName("notification_list") val list: List<Notification>
) : IApiModel

data class Notification(
    val content: String,
    @SerializedName("created_at") val createdAt: Long,
    val id: Int,
    val read: Boolean,
    val title: String,
    val uri: String,
    @SerializedName("sender_user") val senderUser: UserProfile
) : IApiModel