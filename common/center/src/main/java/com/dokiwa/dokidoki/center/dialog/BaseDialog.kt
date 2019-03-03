package com.dokiwa.dokidoki.center.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseDialog(private var mContext: Context, style: Int) : Dialog(mContext, style) {

    private val disposableContainer by lazy { CompositeDisposable() }

    fun addDispose(dispose: Disposable) {
        disposableContainer.add(dispose)
    }

    override fun onDetachedFromWindow() {
        disposableContainer.dispose()
    }

    protected val isAttachedActivityFinishing: Boolean
        get() = mContext is Activity && (mContext as Activity).isFinishing

    /**
     * 调整dialog宽度
     */

    override fun show() {
        if (isAttachedActivityFinishing) {
            // may async invoke by callback
            return
        }
        super.show()
        val dm = mContext.applicationContext.resources.displayMetrics
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
}
