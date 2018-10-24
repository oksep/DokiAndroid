package com.dokiwa.dokidoki.center.plugin.admin

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2018/10/24.
 */

@PluginImplMeta("com.dokiwa.dokidoki.admin.AdminPlugin")
interface IAdminPlugin : FeaturePlugin {

    fun launchAdmin(context: Context)

    companion object {
        fun get(): IAdminPlugin {
            return FeaturePlugin.get(IAdminPlugin::class.java)
        }
    }
}