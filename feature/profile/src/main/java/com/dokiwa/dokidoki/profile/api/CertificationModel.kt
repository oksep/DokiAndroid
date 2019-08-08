package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.plugin.model.UserProfile

data class CertificationWrap(
    val certification: UserProfile.Certification
) : IApiModel