package com.dokiwa.dokidoki.profile.api

import com.google.gson.annotations.SerializedName

data class IdCertifyResult(
    @SerializedName("zhima_certify_url") val zhimaCertifyUrl: String
)