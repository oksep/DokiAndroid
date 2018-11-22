package com.dokiwa.dokidoki.home

import android.app.Activity
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin

/**
 * Created by Septenary on 2018/11/22.
 */
class HomePlugin: IHomePlugin {
    override fun launchHomeActivity(context: Activity) {
        HomeActivity.launch(context)
    }
}