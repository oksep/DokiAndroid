package com.dokiwa.dokidoki.location

import android.app.Activity
import com.dokiwa.dokidoki.center.plugin.location.ILocationPlugin

/**
 * Created by Septenary on 2019-06-30.
 */
class LocationPlugin : ILocationPlugin {

    override fun launchLocationActivity(context: Activity) {
        LocationActivity.launch(context)
    }
}