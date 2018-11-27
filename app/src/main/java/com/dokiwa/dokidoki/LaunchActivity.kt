package com.dokiwa.dokidoki

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.os.HandlerCompat
import com.dokiwa.dokidoki.center.activity.BaseActivity
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.api.subscribeApi
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET


class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    fun onBtnClick(view: View) {
        getAppConfig()

    }

    private fun getAppConfig() {
        Api.get(ConfigApi::class.java).getConfig().subscribeApi(
            this,
            {
                Log.d("MockApi", it.toString())
            },
            {
                Log.e("MockApi", "Request config failed", it)
            }
        )
    }

    private fun delayToHome() {
        HandlerCompat.postDelayed(Handler(), {
            FeaturePlugin.get(IHomePlugin::class.java).launchHomeActivity(this)
            finish()
        }, null, 1000)
    }
}

data class ApiConfig(val status: Status, val data: Data) {
    data class Status(
        @SerializedName("err_msg")
        val errMsg: String,
        val code: Int
    )

    data class Data(
        @SerializedName("image_size_limit")
        val imageSizeLimit: String
    )
}

interface ConfigApi {
    @GET("/api/doki/v1/config")
    fun getConfig(): Observable<ApiConfig>
}