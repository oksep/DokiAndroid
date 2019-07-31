package com.dokiwa.dokidoki.relationship

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin

/**
 * Created by Septenary on 2019-08-01.
 */
class RelationshipPlugin : IRelationshipPlugin {
    override fun launchRelationshipActivity(context: Context) {
        RelationshipActivity.launch(context)
    }
}