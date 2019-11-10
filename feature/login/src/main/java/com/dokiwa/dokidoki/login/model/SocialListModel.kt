package com.dokiwa.dokidoki.login.model

import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.social.SocialHelper
import com.google.gson.annotations.SerializedName

data class SocialListModel(
    @SerializedName("social_list") val list: List<SocialBindUserProfile>?
) {
    fun getWechatBindProfile(): SocialBindUserProfile? {
        return list?.firstOrNull { it.provider == LoginApi.XSocialType.Wechat.type }
    }

    fun getWeiboBindProfile(): SocialBindUserProfile? {
        return list?.firstOrNull { it.provider == LoginApi.XSocialType.Weibo.type }
    }

    fun getQQBindProfile(): SocialBindUserProfile? {
        return list?.firstOrNull { it.provider == LoginApi.XSocialType.QQ.type }
    }
}

data class SocialBindUserProfile(
    val avatar: UserProfile.Avatar?,
    var gender: Int?,
    val nickname: String?,
    val provider: String,
    @SerializedName("provider_name") val providerName: String
)

fun SocialHelper.SocialType.toX(): LoginApi.XSocialType {
    return when (this) {
        SocialHelper.SocialType.QQ -> LoginApi.XSocialType.QQ
        SocialHelper.SocialType.WEIBO -> LoginApi.XSocialType.Weibo
        SocialHelper.SocialType.WECHAT -> LoginApi.XSocialType.Wechat
    }
}