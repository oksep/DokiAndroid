package com.dokiwa.dokidoki.center

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.google.gson.annotations.SerializedName
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.reactivex.Single
import retrofit2.http.GET

@SuppressLint("StaticFieldLeak")
object AppCenter {

    lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        initUmeng()
        initBulgy()
    }

    private fun initUmeng() {
        val appkey = BuildConfig.UMENG_KEY
        val channel = "debug"
        val deviceType = UMConfigure.DEVICE_TYPE_PHONE
        val pushSecret = ""
        UMConfigure.init(context, appkey, channel, deviceType, pushSecret)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        // MobclickAgent.setCatchUncaughtExceptions(BuildConfig.DEBUG)
    }

    private fun initBulgy() {
        CrashReport.initCrashReport(context, BuildConfig.BUGLY_ID, false)
    }

    fun get() = this

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