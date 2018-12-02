package com.dokiwa.dokidoki.home.api.model2

import com.dokiwa.dokidoki.center.api.model.IApiModel

data class UserProfile(
    val avatar: Avatar,
    val birthday: String,
    val certification: Certification,
    val city: City,
    val education: Int,
    val gender: Int,
    val height: Int,
    val income: Int,
    val intro: String,
    val last_active: Int,
    val nickname: String,
    val setting: Setting,
    val user_id: Int,
    val uuid: String
): IApiModel