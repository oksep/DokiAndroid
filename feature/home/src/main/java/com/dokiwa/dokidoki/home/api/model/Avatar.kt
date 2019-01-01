package com.dokiwa.dokidoki.home.api.model

import com.dokiwa.dokidoki.center.api.model.IApiModel

data class Avatar(
    val middle_url: String,
    val raw_url: String,
    val url: String
) : IApiModel