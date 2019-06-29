package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import kotlinx.android.synthetic.main.ui_view_oops.view.*
import kotlinx.android.synthetic.main.ui_view_refresh_layout.view.*
import kotlin.math.abs

/**
 * Created by Septenary on 2019-06-11.
 */
class RefreshRecyclerView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val fadeInAnim: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    private val fadeOutAnim: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)

    init {
        View.inflate(context, R.layout.ui_view_refresh_layout, this)
        this.oopsContainer.gone(false)
        this.recyclerView.gone(false)
        this.refreshLayout.isRefreshing = false
        this.refreshLayout.setColorSchemeResources(R.color.dd_red)
    }

    fun setOnRefreshListener(refreshListener: SwipeRefreshLayout.OnRefreshListener) {
        this.refreshLayout.setRefreshListenerHaptic {
            refreshListener.onRefresh()
        }
    }

    fun showError(@DrawableRes iconResId: Int, @StringRes messageResId: Int) {
        this.refreshLayout.isRefreshing = false
        this.recyclerView.gone()
        this.oopsContainer.visible()
        this.oopsIcon.setImageResource(iconResId)
        this.oopsMessage.setText(messageResId)
    }

    fun showError(icon: Drawable, message: String) {
        this.refreshLayout.isRefreshing = false
        this.oopsContainer.visible()
        this.recyclerView.gone()
        this.oopsIcon.setImageDrawable(icon)
        this.oopsMessage.text = message
    }

    fun showLoading() {
        this.refreshLayout.isRefreshing = true
        this.oopsContainer.gone()
    }

    fun showSuccess(anim: Boolean = true) {
        this.refreshLayout.isRefreshing = false
        this.recyclerView.visible(anim)
        this.oopsContainer.gone(anim)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        this.recyclerView.adapter = adapter
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        this.recyclerView.layoutManager = layoutManager
    }

    fun addItemDecoration(decoration: RecyclerView.ItemDecoration) {
        this.recyclerView.addItemDecoration(decoration)
    }

    fun getRecyclerView(): RecyclerView {
        return this.recyclerView
    }

    fun getRefreshLayout(): SwipeRefreshLayout {
        return this.refreshLayout
    }

    private fun View.gone(anim: Boolean = true) {
        if (visibility != View.GONE) {
            animation = if (anim) fadeOutAnim else null
            visibility = View.GONE
        }
    }

    private fun View.visible(anim: Boolean = true) {
        if (visibility != View.VISIBLE) {
            animation = if (anim) fadeInAnim else null
            visibility = View.VISIBLE
        }
    }

    private var lastX = -1
    private var lastY = -1

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.rawX.toInt()
        val y = ev.rawY.toInt()
        var dealtX = 0
        var dealtY = 0

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = -1
                lastY = -1
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                dealtX += abs(x - lastX)
                dealtY += abs(y - lastY)
                lastX = x
                lastY = y
                if (dealtX / 2 <= dealtY) {
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}