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

    val API_BASE_URL = BuildConfig.API_BASE_URL
    val API_KEY = BuildConfig.API_KEY
    val API_SECRET = BuildConfig.API_SECRET
    val IM_KEY = BuildConfig.IM_KEY
    val IM_SECRET = BuildConfig.IM_SECRET
    val QQ_KEY = BuildConfig.QQ_KEY
    val QQ_SECRET = BuildConfig.QQ_SECRET
    val SESAME_CERT_ID = BuildConfig.SESAME_CERT_ID
    val UMENG_KEY = BuildConfig.UMENG_KEY
    val WEIBO_KEY = BuildConfig.WEIBO_KEY
    val WEIBO_SECRET = BuildConfig.WEIBO_SECRET
    val WX_KEY = BuildConfig.WX_KEY
    val WX_SECRET = BuildConfig.WX_SECRET
    val DEBUG = BuildConfig.DEBUG
    val IMAGE_SIZE_LIMIT: Int
        get() = apiConfig.imageSizeLimit

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