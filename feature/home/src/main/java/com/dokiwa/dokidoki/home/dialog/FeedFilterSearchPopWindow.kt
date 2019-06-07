package com.dokiwa.dokidoki.home.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.home.R

/**
 * Created by Septenary on 2019-06-07.
 */
class FeedFilterSearchPopWindow(private val context: Activity) : PopupWindow(context) {
    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_feed_filter_search, null)
        setContentView(contentView)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isFocusable = true
        isOutsideTouchable = true
        isSplitTouchEnabled = false
        width = WindowManager.LayoutParams.MATCH_PARENT
        setOnDismissListener {
            setBackgroundAlpha(1f)
        }

        (contentView as View).findViewById<Button>(R.id.resetButton).setOnClickListener {
            // AlertDialog.Builder(context).setMessage("AAAAAAA").setNegativeButton("嗯呢", { _, _ -> }).create().show()

            IProfilePlugin.get().getCityPickerDialog(context).show()
        }
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        setBackgroundAlpha(0.75f)
    }

    private fun setBackgroundAlpha(alpha: Float) {
        val lp = context.window.attributes
        lp.alpha = alpha
        context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        context.window.attributes = lp
    }
}