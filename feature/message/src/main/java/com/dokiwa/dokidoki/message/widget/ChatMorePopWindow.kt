package com.dokiwa.dokidoki.message.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.chatroom.ChatRoomActivity

class ChatMorePopWindow(
    private val context: ChatRoomActivity,
    private val contactAccount: String
) : PopupWindow(context) {

    private var isInBlackList: Boolean = false

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.view_chat_more_pop_window, null)
        setContentView(contentView)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
        isSplitTouchEnabled = false

        val banTextView = contentView.findViewById<TextView>(R.id.banBtn)
        banTextView.setOnClickListener {
            dismiss()
            onBanBtnClick()
        }
        contentView.findViewById<View>(R.id.tipOffBtn).setOnClickListener {
            dismiss()
            IRelationshipPlugin.get().launchBanReportUserActivity(context, contactAccount, contactAccount)
        }
        contentView.findViewById<View>(R.id.profileBtn).setOnClickListener {
            dismiss()
            IProfilePlugin.get().launchProfileActivity(context, contactAccount)
        }

        changeBanBtnText()
    }

    private fun onBanBtnClick() {
        if (isInBlackList) {
            AlertDialog.Builder(context)
                .setTitle(R.string.tip)
                .setMessage(R.string.message_chat_un_ban_message)
                .setNegativeButton(R.string.cancel) { d, _ ->
                    d.cancel()
                }
                .setPositiveButton(R.string.confirm) { d, _ ->
                    d.cancel()
                    IRelationshipPlugin.get().delFromBlackList(contactAccount).subscribeApi(context, {
                        isInBlackList = false
                        context.toast(R.string.message_chat_un_baned)
                        changeBanBtnText()
                    })
                }.create().show()
        } else {
            AlertDialog.Builder(context)
                .setTitle(R.string.tip)
                .setMessage(R.string.message_chat_ban_message)
                .setNegativeButton(R.string.cancel) { d, _ ->
                    d.cancel()
                }
                .setPositiveButton(R.string.confirm) { d, _ ->
                    d.cancel()
                    IRelationshipPlugin.get().addToBlackList(contactAccount).subscribeApi(context, {
                        isInBlackList = true
                        context.toast(R.string.message_chat_baned)
                        changeBanBtnText()
                    })
                    context.finish()
                }.create().show()
        }
    }

    private fun changeBanBtnText() {
        contentView.findViewById<TextView>(R.id.banBtn).setText(
            if (isInBlackList)
                R.string.message_chat_un_ban
            else
                R.string.message_chat_ban
        )
    }

    fun preLoad() {
        IRelationshipPlugin.get().isInBlackList(contactAccount).subscribeApi(context, {
            isInBlackList = true
            changeBanBtnText()
        }, {
            Log.e("ChatMorePopWindow", "get isInBlackList failed", it)
        })
    }
}