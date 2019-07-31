package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.dokiwa.dokidoki.message.R

class ChatMorePopWindow(
    context: Context,
    onBanClick: () -> Unit,
    onTipOffClick: () -> Unit,
    onProfileClick: () -> Unit
) : PopupWindow(context) {
    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.view_chat_more_pop_window, null)
        setContentView(contentView)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
        isSplitTouchEnabled = false
        contentView.findViewById<View>(R.id.banBtn).setOnClickListener {
            dismiss()
            onBanClick.invoke()
        }
        contentView.findViewById<View>(R.id.tipOffBtn).setOnClickListener {
            dismiss()
            onTipOffClick.invoke()
        }
        contentView.findViewById<View>(R.id.profileBtn).setOnClickListener {
            dismiss()
            onProfileClick.invoke()
        }
    }
}