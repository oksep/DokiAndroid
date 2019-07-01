package com.dokiwa.dokidoki.location

import androidx.fragment.app.FragmentActivity
import com.dokiwa.dokidoki.center.plugin.location.ILocationPlugin
import com.dokiwa.dokidoki.location.activity.PoiActivity

/**
 * Created by Septenary on 2019-06-30.
 */
class LocationPlugin : ILocationPlugin {
    override fun launchPoiActivity(context: FragmentActivity, onGetPoi: (String, Double, Double) -> Unit) {
        PoiActivity.launch(context, onGetPoi)
    }
}