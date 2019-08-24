package com.dokiwa.dokidoki.center.plugin.model

import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RelationStatus(
    @SerializedName("user_id") val userId: String,
    var following: Boolean = false,
    var follower: Boolean = false
) : IApiModel, Parcelable