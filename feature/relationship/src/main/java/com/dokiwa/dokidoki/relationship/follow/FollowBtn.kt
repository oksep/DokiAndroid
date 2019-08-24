package com.dokiwa.dokidoki.relationship.follow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.dokiwa.dokidoki.relationship.Log
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.relationship.api.RelationApi
import com.dokiwa.dokidoki.relationship.api.RelationStatusWrap
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
                    status.setText(R.string.relation_status_follow_both)
                    status.isSelected = true
                    setUnFollowClickListener()
                } else if (value.follower && !value.following) {
                    status.setText(R.string.relation_status_follow_it)
                    status.isSelected = false
                    setFollowClickListener()
                } else if (!value.follower && value.following) {
                    status.setText(R.string.relation_status_followed)
                    status.isSelected = true
                    setUnFollowClickListener()
                } else {
                    status.setText(R.string.relation_status_follow_it)
                    status.isSelected = false
                    setFollowClickListener()
                }
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
                setOnClickListener(null)
            }
        }

    init {
        View.inflate(context, R.layout.view_relation_status_btn, this)
        relationsStatus = null
        isLoading = false
    }

    private fun setFollowClickListener() {
        setOnClickListener {
            relationsStatus?.userId?.let {
                Api.get(RelationApi::class.java).followUser(it).request()
            }
        }
    }

    private fun setUnFollowClickListener() {
        setOnClickListener {
            relationsStatus?.userId?.let {
                Api.get(RelationApi::class.java).unFollowUser(it).request()
            }
        }
    }

    private fun Single<RelationStatusWrap>.request() {
        isLoading = true
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeApi(context as? CompositeDisposableContext, {
                isLoading = false
                if (it.status.userId == relationsStatus?.userId) {
                    relationsStatus = it.status
                }
            }, {
                isLoading = false
                Log.e("FollowBtn", "follow user failed", it)
            })
    }
}