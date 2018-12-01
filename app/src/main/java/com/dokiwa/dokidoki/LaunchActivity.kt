package com.dokiwa.dokidoki

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.os.HandlerCompat
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin


class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    fun onBtnClick(view: View) {
        toast(AppCenter.get().apiConfig.toString())
    }

    private fun delayToHome() {
        HandlerCompat.postDelayed(Handler(), {
            FeaturePlugin.get(IHomePlugin::class.java).launchHomeActivity(this)
            finish()
        }, null, 1000)
    }
}