package com.dokiwa.dokidoki.home.api.model2

import com.dokiwa.dokidoki.center.api.model.IApiModel

data class City(
    val code: String,
    val name: String,
    val province: Province
): IApiModel