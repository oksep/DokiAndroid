package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dokiwa.dokidoki.ui.R
import kotlinx.android.synthetic.main.ui_view_oops.view.*
import kotlinx.android.synthetic.main.ui_view_refresh_layout.view.*

/**
 * Created by Septenary on 2019-06-11.
 */
class RefreshRecyclerView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        View.inflate(context, R.layout.ui_view_refresh_layout, this)
        this.oopsContainer.gone()
        this.recyclerView.gone()
        this.refreshLayout.isRefreshing = false
        this.refreshLayout.setColorSchemeResources(R.color.dd_red)
    }

    fun setOnRefreshListener(refreshListener: SwipeRefreshLayout.OnRefreshListener) {
        this.refreshLayout.setOnRefreshListener(refreshListener)
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

    fun showSuccess() {
        this.refreshLayout.isRefreshing = false
        this.recyclerView.visible()
        this.oopsContainer.gone()
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

    private fun View.gone() {
        if (visibility != View.GONE) {
            animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
            visibility = View.GONE
        }
    }

    private fun View.visible() {
        if (visibility != View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            visibility = View.VISIBLE
        }
    }
}