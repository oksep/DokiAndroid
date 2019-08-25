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
        View.inflate(context, R.layout.view_relation_status_btn, this)
        isLoading = false
    }

    fun setRelationsStatus(relationStatus: RelationStatus) {
        setUpRelationStatus(
            relationStatus,
            {
                isLoading = it
            },
            { newRelationStatus ->
                if (newRelationStatus.follower && newRelationStatus.following) {
                    status.setText(R.string.relation_status_follow_both)
                    status.isSelected = true
                } else if (newRelationStatus.follower && !newRelationStatus.following) {
                    status.setText(R.string.relation_status_follow_it)
                    status.isSelected = false
                } else if (!newRelationStatus.follower && newRelationStatus.following) {
                    status.setText(R.string.relation_status_followed)
                    status.isSelected = true
                } else {
                    status.setText(R.string.relation_status_follow_it)
                    status.isSelected = false
                }
            },
            false
        )
    }
}

fun View.setUpRelationStatus(
    relationStatus: RelationStatus,
    onLoading: (Boolean) -> Unit,
    onStatusChange: (RelationStatus) -> Unit,
    requestRelationStatus: Boolean = false
) {

    this.tag = relationStatus

    onStatusChange(relationStatus)

    fun Single<RelationStatusWrap>.request() {
        onLoading(true)
        subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeApi(context as? CompositeDisposableContext, {
                onLoading(false)
                if (it.status.userId == (this@setUpRelationStatus.tag as? RelationStatus)?.userId) {
                    setUpRelationStatus(it.status, onLoading, onStatusChange, false)
                }
                Log.d("SetUpRelationStatus", "relation status $it")
            }, {
                onLoading(false)
                Log.e("SetUpRelationStatus", "follow user failed", it)
            })
    }

    fun setFollowClickListener() {
        setOnClickListener {
            (this@setUpRelationStatus.tag as? RelationStatus)?.userId?.let {
                Api.get(RelationApi::class.java).followUser(it).request()
            }
        }
    }

    fun setUnFollowClickListener() {
        setOnClickListener {
            (this@setUpRelationStatus.tag as? RelationStatus)?.userId?.let {
                Api.get(RelationApi::class.java).unFollowUser(it).request()
            }
        }
    }

    relationStatus.apply {
        if (follower && following) {
            setUnFollowClickListener()
        } else if (follower && !following) {
            setFollowClickListener()
        } else if (!follower && following) {
            setUnFollowClickListener()
        } else {
            setFollowClickListener()
        }
    }

    if (requestRelationStatus) {
        val default = RelationStatus(relationStatus.userId, following = false, follower = false)
        Api.get(RelationApi::class.java).getRelationStatusList(default.userId)
            .map { it.list?.getOrNull(0) ?: default }
            .onErrorReturn { default }
            .subscribeApi(context as? CompositeDisposableContext, {
                Log.d("SetUpRelationStatus", "get relation status -> $it")
                setUpRelationStatus(it, onLoading, onStatusChange, false)
            }, {
                Log.e("SetUpRelationStatus", "get relation status failed", it)
            })
    }
}