package com.dokiwa.dokidoki.profile.api

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class SocialListModel(
    @SerializedName("social_list") val list: List<JsonElement>?
)

data class SocialModel(
    val todo: String?
)

enum class XSocialType(val type: String) {
    QQ("qq"),
    Weibo("weibo"),
    Wechat("wechat")
}