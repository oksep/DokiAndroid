package com.dokiwa.dokidoki.login.model

import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Septenary on 2018/12/29.
 * ```
 * {
 *   "access_token": "8wsd8wnadnzbhhrkcr0bu32mw7lv1byg",
 *   "token_type": "mac",
 *   "expires_in": 0,
 *   "refresh_token": "",
 *   "mac_key": "v5ftyfu81ef511wv9yldjegq30suercs",
 *   "mac_algorithm": "hmac-sha-256",
 *   "nim_token": "95d3feaed2077b14bd2c9f137b94fe6c"
 * }
 * ```
 */
@Parcelize
data class UserToken(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("expires_in")
    val expiresIn: Int,

    @SerializedName("mac_algorithm")
    val macAlgorithm: String,

    @SerializedName("mac_key")
    val macKey: String,

    @SerializedName("nim_token")
    val nimToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("token_type")
    val tokenType: String
) : IApiModel, Parcelable

