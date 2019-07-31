package com.dokiwa.dokidoki.message.chatroom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.dokiwa.dokidoki.center.base.activity.BaseSelectImageActivity
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.center.ext.rx.composeMainMain
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.im.IMAudioController
import com.dokiwa.dokidoki.message.im.IMService
import com.dokiwa.dokidoki.message.im.IMSessionMessage
import com.dokiwa.dokidoki.message.widget.ChatMorePopWindow
import com.dokiwa.dokidoki.ui.util.KeyboardHeightProvider
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.io.File
import java.util.concurrent.TimeUnit

class ChatRoomActivity : BaseSelectImageActivity() {

    companion object {
        private const val TAG = "ChatRoomActivity"

        private const val COUNT_PER_PAGE = 20

        private const val EXTRA_CONTACT_ACCOUNT = "extra.user.contact_account"
        private const val EXTRA_NAME = "extra.user.name"

        fun launch(context: Context, profile: UserProfile) {
            launch(context, profile.uuid, profile.nickname)
        }

        fun launch(context: Context, uuid: String, name: String) {
            context.startActivity(
                Intent(context, ChatRoomActivity::class.java)
                    .putExtra(EXTRA_CONTACT_ACCOUNT, uuid)
                    .putExtra(EXTRA_NAME, name)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }

    private val contactAccount by lazy { intent.getStringExtra(EXTRA_CONTACT_ACCOUNT) }

    private val adapter by lazy { ChatRoomAdapter(contactAccount, ::onResendMsgClick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeStatusBarDark()

        setContentView(R.layout.activity_chat_room)

        initView()

        registerListeners()

        loadData()

        IMAudioController.attach(this) {
            adapter.updateRawData(it)
        }
    }

    private fun initView() {
        toolBar.title.text = intent.getStringExtra(EXTRA_NAME)
        toolBar.rightTextView.setOnClickListener {
            ChatMorePopWindow(this, {
                toast("TODO")
            }, {
                toast("TODO")
            }, {
                IProfilePlugin.get().launchProfileActivity(this, contactAccount)
            })
        }
        inputPanel.setInputPanelCallback(::sendMessageTxt, ::sendMessageImg, ::sendMessageAudio, ::sendMessageSticker)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        KeyboardHeightProvider(this).attach(inputPanel)
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

    private fun clearEditText() {
        inputPanel.clearText()
    }

    private fun registerListeners() {
        IMService.attachChatRoomSession(this.lifecycle, contactAccount)

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
        IMService.getChatRoomLocalSessionMessages(null, contactAccount, COUNT_PER_PAGE).subscribe({
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
        val anchor = adapter.data.first()?.sessionMsg?.rawMsg
        IMService.getChatRoomLocalSessionMessages(anchor, contactAccount, COUNT_PER_PAGE)
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
            Log.e(TAG, "resend msg error -> $it")
        }.bind(this)
    }

    private fun sendMessageTxt(message: String) {
        IMService.sendMessageTxt(contactAccount, message).subscribe(::onSendMessage) {
            Log.e(TAG, "send text msg error -> $it")
        }.bind(this)
    }

    private fun sendMessageImg(imgList: List<Uri>) {
        IMService.sendMessageImg(this, contactAccount, imgList).subscribe(::onSendMessage) {
            Log.e(TAG, "send img msg error -> $it")
        }.bind(this)
    }

    private fun sendMessageSticker(path: String) {
        val uris = listOf(Uri.parse("file://android_asset/$path"))
        IMService.sendMessageImg(this, contactAccount, uris).subscribe(::onSendMessage) {
            Log.e(TAG, "send sticker msg error -> $it")
        }.bind(this)
    }

    private fun sendMessageAudio(audioFile: File, audioLength: Long) {
        IMService.sendMessageAudio(contactAccount, audioFile, audioLength).subscribe(::onSendMessage) {
            Log.e(TAG, "send audio msg error -> $it")
        }.bind(this)
    }
}
