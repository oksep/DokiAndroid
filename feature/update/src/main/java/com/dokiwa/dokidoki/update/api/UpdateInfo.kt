package com.dokiwa.dokidoki.update.api

import android.content.Context
import android.os.Parcelable
import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.util.AppUtil

import kotlinx.android.parcel.Parcelize

/**
 * Created by Septenary on 2018/8/20.
 */

@Parcelize
data class Version(
    val version: UpdateInfo
) : IApiModel, Parcelable

@Parcelize
data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val url: String,
    val forceUpdate: Boolean,
    val createdAt: String,
    val changeLog: String? = null,
    val md5: String? = null
) : IApiModel, Parcelable

fun UpdateInfo?.needUpdate(context: Context): Boolean {
    return if (this != null) {
        this.versionCode > AppUtil.getVerCode(context)
    } else {
        false
    }
}