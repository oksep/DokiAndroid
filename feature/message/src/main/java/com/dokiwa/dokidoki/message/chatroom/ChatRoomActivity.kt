package com.dokiwa.dokidoki.message.chatroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.center.ext.rx.composeMainMain
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.im.IMService
import com.dokiwa.dokidoki.message.im.IMSessionMessage
import com.dokiwa.dokidoki.ui.util.KeyboardHeightObserver
import com.dokiwa.dokidoki.ui.util.KeyboardHeightProvider
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.util.concurrent.TimeUnit

class ChatRoomActivity : TranslucentActivity(), KeyboardHeightObserver {

    companion object {
        private const val TAG = "ChatRoomActivity"

        private const val COUNT_PER_PAGE = 20

        private const val EXTRA_UUID = "extra.user.uuid"
        private const val EXTRA_NAME = "extra.user.name"

        fun launch(context: Context, profile: UserProfile) {
            launch(context, profile.uuid, profile.nickname)
        }

        fun launch(context: Context, uuid: String, name: String) {
            context.startActivity(
                Intent(context, ChatRoomActivity::class.java)
                    .putExtra(EXTRA_UUID, uuid)
                    .putExtra(EXTRA_NAME, name)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }

    private val uuid by lazy { intent.getStringExtra(EXTRA_UUID) }

    private val adapter by lazy { ChatRoomAdapter(uuid, ::onResendMsgClick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeStatusBarDark()

        setContentView(R.layout.activity_chat_room)

        initView()

        registerListeners()

        loadData()
    }

    private fun initView() {
        KeyboardHeightProvider(this).attach(this)
        toolBar.title.text = intent.getStringExtra(EXTRA_NAME)
        inputPanel.setInputPannelCallback {
            sendMessage(it)
        }
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
    }

    private fun ensureHeaderFetchMoreView(showHeaderMoreView: Boolean) {
        if (showHeaderMoreView) {
            if (adapter.headerLayoutCount <= 0) {
                adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.view_chat_room_header_more, null))
                adapter.isUpFetchEnable = true
                adapter.setUpFetchListener {
                    loadMoreData()
                }
            }
        } else {
            adapter.isUpFetchEnable = false
            adapter.setUpFetchListener(null)
            adapter.removeAllHeaderView()
        }
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        inputPanel.ensureKeyboardSpace(height)
    }

    private fun clearEditText() {
        inputPanel.clearText()
    }

    private fun registerListeners() {
        IMService.attachChatRoomSession(this.lifecycle, uuid)

        IMService.subscribeChatRoomIncomingMessage().subscribe(::onReceiveNewMessage) {
            Log.e(TAG, "subscribeNimUserInfoList error", it)
        }.bind(this)

        IMService.subscribeNimUserInfoList().subscribe(::onUpdateRecentMessageNimUserInfo) {
            Log.e(TAG, "subscribeNimUserInfoList error", it)
        }.bind(this)

        IMService.subscribeMsgStatus().subscribe(::onUpdateMsgStatus) {
            Log.e(TAG, "subscribeMsgStatus error", it)
        }.bind(this)
    }

    private fun onUpdateRecentMessageNimUserInfo(list: List<NimUserInfo>) {
        var changed = false
        list.forEach { nimUserInfo ->
            adapter.data.find {
                it.sessionMsg.contactUserInfo == null && it.sessionMsg.rawMsg.fromAccount == nimUserInfo.account
            }?.apply {
                sessionMsg.contactUserInfo = nimUserInfo
                changed = true
            }
        }
        if (changed) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun onReceiveNewMessage(list: List<IMSessionMessage>) {
        Log.d(TAG, "receive new messages -> $list")
        adapter.addNewRawData(list)
        recyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun onUpdateMsgStatus(msg: IMSessionMessage) {
        Log.d(TAG, "on update message status -> $msg")
        adapter.updateRawData(msg)
    }

    private fun onSendMessage(msg: IMSessionMessage) {
        Log.d(TAG, "on send message -> $msg")
        adapter.addNewRawData(msg)
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        clearEditText()
    }

    private fun onResendMessage(msg: IMSessionMessage) {
        Log.d(TAG, "on resend message -> $msg")
        adapter.updateRawData(msg)
    }

    private fun loadData() {
        IMService.getChatRoomLocalSessionMessages(null, uuid, COUNT_PER_PAGE).subscribe({
            Log.d(TAG, "get session messages -> ${it.size}, $it")
            adapter.setRawData(it)
            ensureHeaderFetchMoreView(it.size >= COUNT_PER_PAGE)
        }, {
            Log.e(TAG, "get session messages error -> $it")
            ensureHeaderFetchMoreView(false)
        }).bind(this)
    }

    private fun loadMoreData() {
        adapter.isUpFetching = true
        IMService.getChatRoomLocalSessionMessages(adapter.data.first()?.sessionMsg?.rawMsg, uuid, COUNT_PER_PAGE)
            .delay(150, TimeUnit.MILLISECONDS)
            .composeMainMain()
            .subscribe({
                Log.d(TAG, "more session messages -> ${it.size}, $it")
                adapter.addOldRawData(it)
                adapter.isUpFetching = false
                ensureHeaderFetchMoreView(it.size >= COUNT_PER_PAGE)
            }, {
                Log.e(TAG, "more session messages error -> $it")
                adapter.isUpFetching = false
                ensureHeaderFetchMoreView(false)
            }).bind(this)
    }

    private fun onResendMsgClick(msg: IMSessionMessage) {
        IMService.resendTextMessage(msg).subscribe(::onResendMessage) {
            Log.e(TAG, "resend text msg error -> $it")
        }.bind(this)
    }

    private fun sendMessage(message: String) {
        IMService.sendTextMessage(uuid, message).subscribe(::onSendMessage) {
            Log.e(TAG, "send text msg error -> $it")
        }.bind(this)
    }
}
