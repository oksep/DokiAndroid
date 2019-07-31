package com.dokiwa.dokidoki.center.plugin.relationship

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta

/**
 * Created by Septenary on 2019/8/1.
 */
@PluginImplMeta("com.dokiwa.dokidoki.relationship.RelationshipPlugin")
interface IRelationshipPlugin : FeaturePlugin {

    companion object {
        fun get(): IRelationshipPlugin {
            return FeaturePlugin.get(IRelationshipPlugin::class.java)
        }
    }

    fun launchRelationshipActivity(context: Context)
}