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
    val industry: Industry?,
    @SerializedName("keyword_list") val tags: List<Tag>?,
    val education: Int,
    val gender: Int,
    val height: Int,
    val income: Int,
    val intro: String?,
    @SerializedName("last_active") val lastActive: Int,
    val nickname: String,
    val setting: Setting,
    @SerializedName("user_id") val userId: Int,
    val uuid: String,
    @SerializedName("picture_list") val pictures: List<Picture>?
) : IApiModel {

    data class Avatar(
        @SerializedName("middle_url") val middleUrl: String?,
        @SerializedName("raw_url") val rawUrl: String,
        val url: String?
    ) : IApiModel

    data class Picture(
        @SerializedName("middle_url") val middleUrl: String?,
        @SerializedName("raw_url") val rawUrl: String,
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
        @SerializedName("graduation_year") val graduationYear: Int,
        val school: String
    ) : IApiModel

    data class Verify(
        val type: String,
        val title: String
    ) : IApiModel

    data class Industry(
        val id: Int,
        val name: String,
        @SerializedName("parent_industry") val parent: Industry? = null
    ) : IApiModel

    data class Tag(
        val name: String
    ) : IApiModel
}

object Gender {
    const val UNKNOWN = 0 // 未知
    const val MALE = 1 // 男
    const val FEMALE = 2 // 女
}

object Education {
    const val UNKOWN = 0    // 未知
    const val JUNIOR = 1    // 大专
    const val BACHELOR = 2  // 本科
    const val MASTER = 3    // 硕士
    const val PHD = 4       // 博士
    const val LOW = 5       // 中职及以下
}