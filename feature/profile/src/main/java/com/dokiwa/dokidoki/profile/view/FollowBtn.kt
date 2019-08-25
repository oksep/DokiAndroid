package com.dokiwa.dokidoki.profile.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.profile.R
import kotlinx.android.synthetic.main.view_profile_follow_btn.view.*

/**
 * Created by Septenary on 2019-08-22.
 */
class FollowBtn @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isLoading: Boolean = false
        set(value) {
            field = value
            if (value) {
                status.visibility = View.INVISIBLE
                progress.visibility = View.VISIBLE
            } else {
                status.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        }

    init {
        View.inflate(context, R.layout.view_profile_follow_btn, this)
        isLoading = false
    }

    fun setRelationsStatus(
        relationStatus: RelationStatus,
        onRelationStatusChanged: ((RelationStatus) -> Unit)? = null
    ) {
        IRelationshipPlugin.get().setUpRelationStatus(
            view = this,
            relationStatus = relationStatus,
            onLoading = {
                isLoading = it
            },
            onStatusChange = { newRelationStatus ->
                if (newRelationStatus.follower && newRelationStatus.following) {
                    status.setText(R.string.profile_detail_relation_status_follow_both)
                    status.setStatusIcon(R.drawable.profile_ic_relation_both)
                    status.isSelected = true
                } else if (newRelationStatus.follower && !newRelationStatus.following) {
                    status.setText(R.string.profile_detail_relation_status_follow_it)
                    status.setStatusIcon(R.drawable.profile_ic_relation_follow)
                    status.isSelected = false
                } else if (!newRelationStatus.follower && newRelationStatus.following) {
                    status.setText(R.string.profile_detail_relation_status_followed)
                    status.setStatusIcon(R.drawable.profile_ic_relation_followed)
                    status.isSelected = true
                } else {
                    status.setText(R.string.profile_detail_relation_status_follow_it)
                    status.setStatusIcon(R.drawable.profile_ic_relation_follow)
                    status.isSelected = false
                }
                onRelationStatusChanged?.invoke(newRelationStatus)
            },
            requestRelationStatus = true
        )
    }

    private fun TextView.setStatusIcon(@DrawableRes resId: Int) {
        status.setCompoundDrawablesWithIntrinsicBounds(
            ResourcesCompat.getDrawable(resources, resId, null),
            null,
            null,
            null
        )
    }
}