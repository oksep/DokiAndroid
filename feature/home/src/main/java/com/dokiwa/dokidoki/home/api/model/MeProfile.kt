package com.dokiwa.dokidoki.home.api.model

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.plugin.model.UserProfile

/**
 * Created by Septenary on 2019/1/2.
 */
data class MeProfile(val profile: UserProfile) : IApiModel