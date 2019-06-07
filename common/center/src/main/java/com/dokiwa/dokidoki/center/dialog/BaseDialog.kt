package com.dokiwa.dokidoki.center.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dokiwa.dokidoki.ui.util.NotchHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseDialog(context: Context, style: Int) : Dialog(context, style) {

    private var contentView: View? = null

    private val disposableContainer by lazy { CompositeDisposable() }

    fun addDispose(dispose: Disposable) {
        disposableContainer.add(dispose)
    }

    override fun onDetachedFromWindow() {
        disposableContainer.dispose()
    }

    override fun setContentView(layoutResID: Int) {
        setContentView(LayoutInflater.from(context).inflate(layoutResID, null))
    }

    override fun setContentView(view: View) {
        contentView = view
        super.setContentView(view)
    }

    protected val isAttachedActivityFinishing: Boolean
        get() = context is Activity && (context as Activity).isFinishing

    /**
     * 调整dialog宽度
     */
    override fun show() {
        if (isAttachedActivityFinishing) {
            // may async invoke by callback
            return
        }
        super.show()
        val dm = context.applicationContext.resources.displayMetrics
        val params = window!!.attributes
        params.width = dm.widthPixels
        window!!.attributes = params
    }

    protected fun matchParent() {
        val attributes = window?.attributes
        attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        attributes?.height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.attributes = attributes
    }

    protected fun fitNotch() {
        if (NotchHelper.isNotch(context) && contentView != null) {
            val rootLayout = contentView!!
            val marginLayoutParams = rootLayout.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = NotchHelper.getNotchStatusBarHeight(context)
            rootLayout.layoutParams = marginLayoutParams
        }
    }
}
