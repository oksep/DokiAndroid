package com.dokiwa.dokidoki.timeline.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.model.RelationStatus
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    var relationsStatus: RelationStatus? = null
        set(value) {
            field = value
            if (value?.following == false) {
                status.setText(R.string.timeline_follow)
                status.isSelected = false
                visibility = View.VISIBLE
                setFollowClickListener()
            } else {
                visibility = View.GONE
                setOnClickListener(null)
            }
        }

    var onRelationStatusChanged: ((RelationStatus) -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_timeline_relation_status_btn, this)
        relationsStatus = null
        isLoading = false
    }

    private fun setFollowClickListener() {
        setOnClickListener {
            relationsStatus?.userId?.let { it1 ->
                isLoading = true
                IRelationshipPlugin.get().followUser(it1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeApi(context as? CompositeDisposableContext, {
                        isLoading = false
                        if (it?.userId == relationsStatus?.userId) {
                            relationsStatus = it
                            onRelationStatusChanged?.invoke(it)
                        }
                    }, {
                        isLoading = false
                        Log.e("FollowBtn", "follow user failed", it)
                    })
            }
        }
    }
}