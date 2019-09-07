package com.dokiwa.dokidoki.relationship

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.relationship.api.RelationApi
import com.dokiwa.dokidoki.relationship.api.toRelationStatusPair
import com.dokiwa.dokidoki.relationship.blacklist.BanReportActivity
import com.dokiwa.dokidoki.relationship.blacklist.BlackListActivity
import com.dokiwa.dokidoki.relationship.feedback.FeedbackActivity
import com.dokiwa.dokidoki.relationship.follow.FollowingFragment
import com.dokiwa.dokidoki.relationship.follow.RelationshipActivity
import com.dokiwa.dokidoki.relationship.follow.setUpRelationStatus
import io.reactivex.Single

/**
 * Created by Septenary on 2019-08-01.
 */
class RelationshipPlugin : IRelationshipPlugin {
    override fun launchRelationshipActivity(context: Context, isFollowing: Boolean) {
        RelationshipActivity.launch(context, isFollowing)
    }

    override fun launchBlackListActivity(context: Context) {
        BlackListActivity.launch(context)
    }

    override fun launchBanReportUserActivity(context: Context, userUUID: String, reportId: String) {
        BanReportActivity.launch(context, userUUID, reportId, BanReportActivity.ObjectType.USER)
    }

    override fun launchBanReportTimelineActivity(context: Context, userUUID: String, reportId: String) {
        BanReportActivity.launch(context, userUUID, reportId, BanReportActivity.ObjectType.TIMELINE)
    }

    override fun launchBanReportCommentActivity(context: Context, userUUID: String, reportId: String) {
        BanReportActivity.launch(context, userUUID, reportId, BanReportActivity.ObjectType.COMMENT)
    }

    override fun launchFeedbackActivity(context: Context) {
        FeedbackActivity.launch(context)
    }

    override fun isInBlackList(uuid: String): Single<Boolean> {
        return IProfilePlugin.get()
            .getUserProfile(uuid)
            .flatMap { Api.get(RelationApi::class.java).isInBlackList(it.profile.userId.toString()) }
            .map { it.exists }
    }

    override fun addToBlackList(uuid: String): Single<Boolean> {
        return IProfilePlugin.get()
            .getUserProfile(uuid)
            .flatMap { Api.get(RelationApi::class.java).addToBlackList(it.profile.userId.toString()) }
            .map { true }
    }

    override fun delFromBlackList(uuid: String): Single<Boolean> {
        return IProfilePlugin.get()
            .getUserProfile(uuid)
            .flatMap { Api.get(RelationApi::class.java).delFromBlackList(it.profile.userId.toString()) }
            .map { true }
    }

    override fun getDevFragment(): Fragment {
        return FollowingFragment()
    }

    override fun <T> toRelationStatusPair(
        req: Single<List<T>>,
        getId: (T) -> String
    ): Single<List<Pair<T, RelationStatus>>> {
        return req.toRelationStatusPair(getId)
    }

    override fun setUpRelationStatus(
        view: View,
        relationStatus: RelationStatus,
        onLoading: (Boolean) -> Unit,
        onStatusChange: (RelationStatus) -> Unit,
        requestRelationStatus: Boolean
    ) {
        view.setUpRelationStatus(relationStatus, onLoading, onStatusChange, requestRelationStatus)
    }
}