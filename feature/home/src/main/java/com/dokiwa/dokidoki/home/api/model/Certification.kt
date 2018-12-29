package com.dokiwa.dokidoki.home.api.model

import com.dokiwa.dokidoki.center.api.model.IApiModel

data class Certification(
    val education: Any,
    val identification: Identification
): IApiModel