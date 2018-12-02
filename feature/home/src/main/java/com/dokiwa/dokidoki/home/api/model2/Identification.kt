package com.dokiwa.dokidoki.home.api.model2

import com.dokiwa.dokidoki.center.api.model.IApiModel

data class Identification(
    val name: String,
    val number: String
) : IApiModel