package com.dokiwa.dokidoki.home.api.model

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.plugin.model.UserProfile

/**
 * Created by Septenary on 2018/12/2.
 */

data class Feed(
    val time: Int,
    val type: String,
    val user_profile: UserProfile
) : IApiModel