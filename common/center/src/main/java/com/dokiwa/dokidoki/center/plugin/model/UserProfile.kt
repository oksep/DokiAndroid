package com.dokiwa.dokidoki.center.plugin.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.util.birthDayToAge
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class UserProfileWrap(val profile: UserProfile) : IApiModel

@SuppressLint("ParcelCreator")
@Parcelize
data class UserProfile(
    val avatar: Avatar,
    val birthday: String,
    val certification: Certification,
    val verify: Verify?,
    val city: City?,
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
) : IApiModel, Parcelable {

    @Parcelize
    data class Avatar(
        @SerializedName("middle_url") val middleUrl: String?,
        @SerializedName("raw_url") val rawUrl: String,
        val url: String?
    ) : IApiModel, Parcelable {
        fun adaptUrl(): String {
            return (middleUrl ?: url) ?: rawUrl
        }
    }

    @Parcelize
    data class Picture(
        @SerializedName("middle_url") val middleUrl: String?,
        @SerializedName("raw_url") val rawUrl: String,
        val url: String?
    ) : IApiModel, Parcelable {
        fun adaptUrl(): String {
            return (middleUrl ?: url) ?: rawUrl
        }
    }

    @Parcelize
    data class Certification(
        // TODO: 2019/3/3 @Septenary education!
        val education: Education?,
        val identification: Identification?
    ) : IApiModel, Parcelable

    @Parcelize
    data class Province(
        val name: String
    ) : IApiModel, Parcelable

    @Parcelize
    data class Setting(
        @SerializedName("allow_recommend") val allowRecommend: Boolean,
        @SerializedName("certificated_only") val certificatedOnly: Boolean
    ) : IApiModel, Parcelable

    @Parcelize
    data class City(
        val code: String,
        val name: String,
        val province: Province
    ) : IApiModel, Parcelable

    @Parcelize
    data class Identification(
        val name: String,
        val number: String
    ) : IApiModel, Parcelable

    @Parcelize
    data class Education(
        val education: String,
        @SerializedName("graduation_year") val graduationYear: Int,
        val school: String
    ) : IApiModel, Parcelable

    @Parcelize
    data class Verify(
        val type: String,
        val title: String
    ) : IApiModel, Parcelable

    @Parcelize
    data class Industry(
        val id: Int,
        val name: String,
        @SerializedName("parent_industry") val parent: Industry? = null
    ) : IApiModel, Parcelable

    @Parcelize
    data class Tag(
        val name: String
    ) : IApiModel, Parcelable

    fun assembleAddressPosition(): String {
        val profile = this
        val idsty = profile.industry?.name
        return profile.city?.name ?: "" + if (idsty.isNullOrEmpty()) "" else " | $idsty"
    }

    fun assembleAgeHeightEdu(): String {
        val profile = this
        val age = profile.birthday.birthDayToAge()
        val edu = profile.education.educationToString()
        val height = if (profile.height > 9) " | ${profile.height}cm" else ""
        val edus = if (edu.isNullOrEmpty()) "" else " | $edu"
        return "${age}岁$height$edus"
    }

    private fun Int.educationToString(): String? {
        return when (this) {
            com.dokiwa.dokidoki.center.plugin.model.Education.JUNIOR -> "大专"
            com.dokiwa.dokidoki.center.plugin.model.Education.BACHELOR -> "本科"
            com.dokiwa.dokidoki.center.plugin.model.Education.MASTER -> "硕士"
            com.dokiwa.dokidoki.center.plugin.model.Education.PHD -> "博士"
            else -> ""
        }
    }
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