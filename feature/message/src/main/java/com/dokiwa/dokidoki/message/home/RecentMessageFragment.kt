package com.dokiwa.dokidoki.message.home

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.chatroom.ChatRoomActivity
import com.dokiwa.dokidoki.message.im.IMLoginStatus
import com.dokiwa.dokidoki.message.im.IMRecentMessage
import com.dokiwa.dokidoki.message.im.IMService
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.fragment_recent_message.*

private const val TAG = "MessageFragment"

class MessageFragment : BaseShareFragment(R.layout.fragment_recent_message) {

    private val adapter by lazy { RecentMessageAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        registerIMLoginStatusListener()
        registerIMClientsListener()
        registerIMRecenterMessageListener()
        registerIMUserInfoListener()

        loadData()
    }

    private fun initView() {

        adapter.setOnItemClickListener { adapter, view, position ->
            (adapter.data.getOrNull(position) as? IMRecentMessage)?.let {
                ChatRoomActivity.launch(requireContext(), it.contactId, it.contactUserInfo?.name ?: "")
            }
        }

        refreshRecyclerView.getRecyclerView().addItemDecoration(LineDivider(requireContext()))
        refreshRecyclerView.setAdapter(adapter)
        refreshRecyclerView.getRefreshLayout().setRefreshListenerHaptic {
            loadData()
        }
    }

    private fun registerIMLoginStatusListener() {
        IMService.subscribeLoginStatus().subscribe(::updateLoginStatus) {
            Log.e(TAG, "subscribeLoginStatus error", it)
        }.bind(this)
    }

    private fun registerIMClientsListener() {
        IMService.subscribeOnlineClients().subscribe({
            Log.d(TAG, "subscribeOnlineClients clients -> $it")
        }, {
            Log.e(TAG, "subscribeOnlineClients error", it)
        }).bind(this)
    }

    private fun registerIMRecenterMessageListener() {
        IMService.subscribeRecentMessages().subscribe(::updateData) {
            Log.e(TAG, "subscribeRecentMessages error", it)
        }.bind(this)
    }

    private fun registerIMUserInfoListener() {
        IMService.subscribeNimUserInfoList()
            .subscribe(::updateRecentMessageNimUserInfo) {
                Log.e(TAG, "subscribeNimUserInfoList error", it)
            }.bind(this)
    }

    private fun loadData() {
        refreshRecyclerView.showLoading()
        IMService.getRecentMessages().subscribe({
            resetData(it)
            refreshRecyclerView.showSuccess()
        }, {
            Log.e(TAG, "getRecentMessages error", it)
            refreshRecyclerView.showError(R.drawable.ui_ic_oops_box, R.string.message_home_page_recent_message_empty)
        }).bind(this)
    }

    private fun resetData(list: List<IMRecentMessage>) {
        Log.d(TAG, "recent message data reset -> $list")
        adapter.setNewData(list)
    }

    private fun updateData(list: List<IMRecentMessage>) {
        Log.d(TAG, "recent message data update -> $list")
        val oldList = adapter.data.filter { m1 -> list.find { m2 -> m1.contactId == m2.contactId } == null }
        val newData = list + oldList
        adapter.setNewData(newData)
    }

    private fun updateRecentMessageNimUserInfo(list: List<NimUserInfo>) {
        list.forEach { nimUserInfo ->
            adapter.data.find {
                it.contactUserInfo == null && it.contactId == nimUserInfo.account
            }?.apply {
                contactUserInfo = nimUserInfo
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateLoginStatus(status: IMLoginStatus) {
        Log.e(TAG, "loginStatus changed -> $status")
        toolBar.title.setText(
            if (status == IMLoginStatus.LOGINED)
                R.string.message_home_page_title
            else
                R.string.message_home_page_title_broken
        )
    }
}