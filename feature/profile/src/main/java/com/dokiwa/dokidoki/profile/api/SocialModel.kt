package com.dokiwa.dokidoki.profile.api

data class SocialListModel(
    val todo: String?
)

data class SocialModel(
    val todo: String?
)

enum class XSocialType(val type: String) {
    QQ("qq"),
    Weibo("weibo"),
    Wechat("wechat")
}