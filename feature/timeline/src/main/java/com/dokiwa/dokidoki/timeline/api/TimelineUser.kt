package com.dokiwa.dokidoki.timeline.api

import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimelineUser(
    val id: String,
    val uuid: String,
    val nickname: String,
    val avatar: UserProfile.Avatar
) : IApiModel, Parcelable