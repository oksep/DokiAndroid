package com.dokiwa.dokidoki.timeline.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.timeline.R
import kotlinx.android.synthetic.main.view_timeline_relation_status_btn.view.*

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
        View.inflate(context, R.layout.view_timeline_relation_status_btn, this)
        isLoading = false
    }

    fun setRelationsStatus(relationStatus: RelationStatus, onRelationStatusChanged: (RelationStatus) -> Unit) {
        IRelationshipPlugin.get().setUpRelationStatus(
            view = this,
            relationStatus = relationStatus,
            onLoading = {
                isLoading = it
            },
            onStatusChange = {
                if (!it.following) {
                    status.setText(R.string.timeline_follow)
                    status.isSelected = false
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                    setOnClickListener(null)
                }
                onRelationStatusChanged.invoke(it)
            },
            requestRelationStatus = false
        )
    }
}