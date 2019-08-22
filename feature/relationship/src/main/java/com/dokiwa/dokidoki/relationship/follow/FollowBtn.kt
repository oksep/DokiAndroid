package com.dokiwa.dokidoki.relationship.follow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.relationship.api.RelationStatus
import kotlinx.android.synthetic.main.view_relation_status_btn.view.*

/**
 * Created by Septenary on 2019-08-22.
 */
class FollowBtn @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var isLoading: Boolean = false
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

    var relationsStatus: RelationStatus? = null
        set(value) {
            field = value
            if (value != null) {
                if (value.follower && value.following) {
                    status.text = "互相关注"
                    status.isSelected = true
                } else if (value.follower && !value.following) {
                    status.text = "关注TA"
                    status.isSelected = false
                } else if (!value.follower && value.following) {
                    status.text = "已关注"
                    status.isSelected = true
                } else {
                    status.text = "关注TA"
                    status.isSelected = false
                }
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }

    init {
        View.inflate(context, R.layout.view_relation_status_btn, this)
        relationsStatus = RelationStatus(0, following = true, follower = false)
        setOnClickListener {
            isLoading = true
            postDelayed({
                isLoading = false
                relationsStatus = relationsStatus?.copy(following = false)
            }, 1000)
        }
    }
}