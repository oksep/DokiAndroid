package com.dokiwa.dokidoki.center.plugin.relationship

import android.content.Context
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.PluginImplMeta
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import io.reactivex.Single

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

    fun launchRelationshipActivity(context: Context, isFollowing: Boolean)

    fun launchBlackListActivity(context: Context)

    fun launchBanReportUserActivity(context: Context, userUUID: String, reportId: String)

    fun launchBanReportTimelineActivity(context: Context, userUUID: String, reportId: String)

    fun launchBanReportCommentActivity(context: Context, userUUID: String, reportId: String)

    fun launchFeedbackActivity(context: Context)

    fun isInBlackList(uuid: String): Single<Boolean>

    fun addToBlackList(uuid: String): Single<Boolean>

    fun delFromBlackList(uuid: String): Single<Boolean>

    fun getDevFragment(): Fragment
    fun <T> toRelationStatusPair(req: Single<List<T>>, getId: (T) -> Int): Single<List<Pair<T, RelationStatus?>>>
}