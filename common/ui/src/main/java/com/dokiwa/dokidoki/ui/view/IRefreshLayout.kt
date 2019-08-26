package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic

/**
 * Created by Septenary on 2019-06-11.
 */
interface IRefreshLayout {

    fun initRefreshLayout(context: Context, refreshLayout: SwipeRefreshLayout, oopsContainer: View, contentView: View)

    fun setOnRefreshListener(refreshListener: SwipeRefreshLayout.OnRefreshListener)

    fun showError(@DrawableRes iconResId: Int, @StringRes messageResId: Int)

    fun showError(icon: Drawable, message: String)

    fun showLoading()

    fun showSuccess(anim: Boolean = true)
}

class DefaultRefreshLayoutHandler : IRefreshLayout {

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var oopsContainer: View
    private lateinit var contentView: View
    private lateinit var context: Context

    private val oopsIcon: ImageView?
        get() = this.oopsContainer.findViewById(R.id.oopsIcon)

    private val oopsMessage: TextView?
        get() = this.oopsContainer.findViewById(R.id.oopsMessage)

    private val fadeInAnim: Animation by lazy { AnimationUtils.loadAnimation(context, android.R.anim.fade_in) }
    private val fadeOutAnim: Animation by lazy { AnimationUtils.loadAnimation(context, android.R.anim.fade_out) }

    override fun initRefreshLayout(context: Context, refreshLayout: SwipeRefreshLayout, oopsContainer: View, contentView: View) {
        this.context = context
        this.refreshLayout = refreshLayout
        this.oopsContainer = oopsContainer
        this.contentView = contentView

        this.refreshLayout.isRefreshing = false
        this.refreshLayout.setColorSchemeResources(R.color.dd_red)
        this.oopsContainer.gone(false)
        this.contentView.gone(false)
    }

    override fun setOnRefreshListener(refreshListener: SwipeRefreshLayout.OnRefreshListener) {
        this.refreshLayout.setRefreshListenerHaptic {
            refreshListener.onRefresh()
        }
    }

    override fun showError(iconResId: Int, messageResId: Int) {
        this.refreshLayout.isRefreshing = false
        this.contentView.gone()
        this.oopsContainer.visible()
        this.oopsIcon?.setImageResource(iconResId)
        this.oopsMessage?.setText(messageResId)
    }

    override fun showError(icon: Drawable, message: String) {
        this.refreshLayout.isRefreshing = false
        this.contentView.gone()
        this.oopsContainer.visible()
        this.oopsIcon?.setImageDrawable(icon)
        this.oopsMessage?.text = message
    }

    override fun showLoading() {
        this.refreshLayout.isRefreshing = true
        this.oopsContainer.gone()
    }

    override fun showSuccess(anim: Boolean) {
        this.refreshLayout.isRefreshing = false
        this.contentView.visible(anim)
        this.oopsContainer.gone(anim)
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
}