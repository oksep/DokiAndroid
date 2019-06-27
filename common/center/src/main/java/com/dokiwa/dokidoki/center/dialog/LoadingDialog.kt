package com.dokiwa.dokidoki.center.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dokiwa.dokidoki.center.R

/**
 * Created by Septenary on 2019/3/2.
 */
class LoadingDialog private constructor(context: Context, full: Boolean, style: Int) : BaseDialog(context, style) {
    private var msgTextView: TextView? = null

    private var isFirstShow = true

    init {
        val inflater = LayoutInflater.from(context)
        val contentView = inflater.inflate(R.layout.ui_view_loading_dialog, null)
        msgTextView = contentView.findViewById<View>(R.id.message) as TextView
        msgTextView?.visibility = View.GONE

        if (full) {
            val dm = context.applicationContext.resources.displayMetrics
            setContentView(contentView, ViewGroup.LayoutParams(dm.widthPixels, dm.heightPixels))
        } else {
            setContentView(contentView)
        }
    }

    fun setText(text: String) {
        if (!TextUtils.isEmpty(text)) {
            msgTextView?.text = text
            msgTextView?.visibility = View.VISIBLE
        } else {
            msgTextView?.visibility = View.GONE
        }
    }

    fun setText(textResId: Int) {
        setText(context.resources.getString(textResId))
    }

    override fun show() {
        setDialogAnim(this, R.anim.ui_anim_fade_in)
        super.show()
        if (isFirstShow) {
            val dm = context.applicationContext.resources.displayMetrics
            val pullY = dm.heightPixels / 20
            val params = this.window?.attributes
            params?.y = params?.y?.minus(pullY)
            this.window?.attributes = params
            isFirstShow = false
        }
    }

    override fun dismiss() {
        setDialogAnim(this, R.anim.ui_anim_fade_out)
        super.dismiss()
    }

    /**
     * 设置Dialog进出动画
     */
    private fun setDialogAnim(dialog: Dialog, resId: Int) {
        if ((context as? Activity)?.isFinishing == true) {
            return
        }
        if ((context as? Activity)?.isDestroyed == true) {
            return
        }
        val window = dialog.window
        window?.setWindowAnimations(resId)
        val wl = window?.attributes
        wl?.x = 0
        wl?.y = 0
        dialog.onWindowAttributesChanged(wl)
    }

    companion object {

        fun create(context: Context): LoadingDialog {
            val dialog = create(context, false)
            dialog.setCancelable(false)
            return dialog
        }

        fun create(context: Context, fullScreen: Boolean): LoadingDialog {
            return if (fullScreen) {
                LoadingDialog(context, fullScreen, R.style.FullLoadingDialog)
            } else {
                LoadingDialog(context, fullScreen, R.style.NormalLoadingDialog)
            }
        }
    }
}
