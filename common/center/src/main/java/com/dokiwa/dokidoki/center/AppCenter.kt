package com.dokiwa.dokidoki.center

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET

class AppCenter private constructor(val context: Context) {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var appCenter: AppCenter

        fun init(context: Context) {
            appCenter = AppCenter(context)
            appCenter.getApiConfig()
        }

        fun get() = appCenter
    }

    private var apiConfig: ApiConfig = ApiConfig(imageSizeLimit = 1024 * 1024 * 24)

    private fun getApiConfig() {
        Api.get(CenterApi::class.java).getConfig().subscribeApi(
            null,
            {
                apiConfig = it
                Log.d("MockApi", it.toString())
            },
            {
                Log.e("MockApi", "Request config failed", it)
            }
        )
    }
}

data class ApiConfig(
    // 上传文件大小限制，单位 byte
    @SerializedName("image_size_limit")
    val imageSizeLimit: Int
)

interface CenterApi {
    @GET("/api/doki/v1/config")
    fun getConfig(): Single<ApiConfig>
}