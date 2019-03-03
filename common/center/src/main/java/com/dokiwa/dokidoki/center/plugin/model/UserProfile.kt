package com.dokiwa.dokidoki.center.plugin.model

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName

data class UserProfileWrap(val profile: UserProfile) : IApiModel

data class UserProfile(
    val avatar: Avatar,
    val birthday: String,
    val certification: Certification,
    val verify: Verify?,
    val city: City,
    val education: Int,
    val gender: Int,
    val height: Int,
    val income: Int,
    val intro: String,
    @SerializedName("last_active") val lastActive: Int,
    val nickname: String,
    val setting: Setting,
    @SerializedName("user_id") val userId: Int,
    val uuid: String
) : IApiModel {

    data class Avatar(
        @SerializedName("middle_url") val middleUrl: String?,
        @SerializedName("raw_url") val rawUrl: String?,
        val url: String?
    ) : IApiModel

    data class Certification(
        // TODO: 2019/3/3 @Septenary education!
        val education: Education?,
        val identification: Identification?
    ) : IApiModel

    data class Province(
        val name: String
    ) : IApiModel


    data class Setting(
        @SerializedName("allow_recommend") val allowRecommend: Boolean,
        @SerializedName("certificated_only") val certificatedOnly: Boolean
    ) : IApiModel


    data class City(
        val code: String,
        val name: String,
        val province: Province
    ) : IApiModel

    data class Identification(
        val name: String,
        val number: String
    ) : IApiModel

    data class Education(
        val education: String,
        val graduation_year: Int,
        val school: String
    ) : IApiModel

    data class Verify(
        val type: String,
        val title: String
    ) : IApiModel
}
