package com.dokiwa.dokidoki.timeline.api

import com.google.gson.annotations.SerializedName

data class TimelineCommentList(
    @SerializedName("ufeed_comment_list") val commentList: List<TimelineComment>
)

data class TimelineComment(
    val content: String,
    val id: Int,
    val user: TimelineUser,
    @SerializedName("create_time") val createTime: Int,
    @SerializedName("reply_to_user") val replyToUser: TimelineUser?
)

data class CreateCommentResult(
    @SerializedName("ufeed_comment") val comment: TimelineComment
)