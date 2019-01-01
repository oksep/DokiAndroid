package com.dokiwa.dokidoki.home.api.model

import com.dokiwa.dokidoki.center.api.model.IApiModel

data class Setting(
    val allow_recommend: Boolean,
    val certificated_only: Boolean
): IApiModel