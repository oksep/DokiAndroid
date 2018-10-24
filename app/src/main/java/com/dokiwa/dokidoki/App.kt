package com.dokiwa.dokidoki

import android.app.Application
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin

/**
 * Created by Septenary on 2018/10/24.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FeaturePlugin.init(this)
    }
}