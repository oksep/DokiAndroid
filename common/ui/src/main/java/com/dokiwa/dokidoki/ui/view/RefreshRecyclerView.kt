package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
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
        this.oopsContainer.visibility = View.GONE
        this.recyclerView.visibility = View.GONE
        this.refreshLayout.isRefreshing = false
        this.refreshLayout.setColorSchemeResources(R.color.dd_red)
    }

    fun setOnRefreshListener(refreshListener: SwipeRefreshLayout.OnRefreshListener) {
        this.refreshLayout.setOnRefreshListener(refreshListener)
    }

    fun showError(@DrawableRes iconResId: Int, @StringRes messageResId: Int) {
        this.refreshLayout.isRefreshing = false
        this.recyclerView.visibility = View.GONE
        this.oopsContainer.visibility = View.VISIBLE
        this.oopsIcon.setImageResource(iconResId)
        this.oopsMessage.setText(messageResId)
    }

    fun showError(icon: Drawable, message: String) {
        this.refreshLayout.isRefreshing = false
        this.oopsContainer.visibility = View.VISIBLE
        this.recyclerView.visibility = View.GONE
        this.oopsIcon.setImageDrawable(icon)
        this.oopsMessage.text = message
    }

    fun showLoading() {
        this.refreshLayout.isRefreshing = true
        this.oopsContainer.visibility = View.GONE
    }

    fun showSuccess() {
        this.refreshLayout.isRefreshing = false
        this.recyclerView.visibility = View.VISIBLE
        this.oopsContainer.visibility = View.GONE
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        this.recyclerView.adapter = adapter
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        this.recyclerView.layoutManager = layoutManager
    }
}