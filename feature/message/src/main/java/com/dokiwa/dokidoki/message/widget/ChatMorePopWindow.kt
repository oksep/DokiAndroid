package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.message.R

class ChatMorePopWindow(
    context: Context,
    contactAccount: String
) : PopupWindow(context) {
    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.view_chat_more_pop_window, null)
        setContentView(contentView)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
        isSplitTouchEnabled = false
        contentView.findViewById<View>(R.id.banBtn).setOnClickListener {
            dismiss()
        }
        contentView.findViewById<View>(R.id.tipOffBtn).setOnClickListener {
            dismiss()
        }
        contentView.findViewById<View>(R.id.profileBtn).setOnClickListener {
            dismiss()
            IProfilePlugin.get().launchProfileActivity(context, contactAccount)
        }
    }
}