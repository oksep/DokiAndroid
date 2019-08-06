package com.dokiwa.dokidoki.relationship

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.relationship.blacklist.BanReportActivity
import com.dokiwa.dokidoki.relationship.blacklist.BlackListActivity
import com.dokiwa.dokidoki.relationship.feedback.FeedbackActivity
import com.dokiwa.dokidoki.relationship.follow.RelationshipActivity

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
}