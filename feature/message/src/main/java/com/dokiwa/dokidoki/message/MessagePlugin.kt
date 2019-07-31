package com.dokiwa.dokidoki.message

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeLog
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.message.chatroom.ChatRoomActivity
import com.dokiwa.dokidoki.message.dialog.KickOutDialog
import com.dokiwa.dokidoki.message.home.MessageFragment
import com.dokiwa.dokidoki.message.im.IMLoginStatus
import com.dokiwa.dokidoki.message.im.IMService
import com.netease.nimlib.sdk.util.NIMUtil

/**
 * Created by Septenary on 2018/10/24.
 * NIM 模块
 */

private const val TAG = "MessagePlugin"

class MessagePlugin : IMessagePlugin {

    @SuppressLint("CheckResult")
    override fun onInit(context: Context) {
        Log.d(TAG, "init nim sdk")
        IMService.init(context)
        if (NIMUtil.isMainProcess(context)) {
            IMService.subscribeLoginStatus().subscribe({
                Log.d(TAG, "online status changed -> $it")
                if (it == IMLoginStatus.KICK_OUT) {
                    KickOutDialog.launch(context)
                }
            }, {
                Log.e(TAG, "online status error", it)
            })
            IMService.getRecentMessages().subscribeLog(TAG, "recentMessages")
        }
    }

    override fun obtainHomeMessageFragment(): Fragment {
        return MessageFragment()
    }

    override fun loginIM() {
        Log.w(TAG, "log in im")
        IMService.loginIM()
    }

    override fun logoutIM() {
        Log.w(TAG, "log out im")
        IMService.logoutIM()
    }

    override fun launchChatRoom(context: Context, userProfile: UserProfile) {
        ChatRoomActivity.launch(context, userProfile)
    }
}