package com.dokiwa.dokidoki.message.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.chatroom.ChatRoomActivity
import com.dokiwa.dokidoki.message.im.*
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.fragment_recent_message.*

private const val TAG = "MessageFragment"

// 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
const val TAG_STICKY = 0x0000_0000_0000_0001L // 联系人置顶tag

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

        adapter.setOnItemLongClickListener { adapter, view, position ->
            (adapter.getItem(position) as? IMRecentMessage)?.let { recentMsg ->
                val isSticky = recentMsg.rawData.hasTag(TAG_STICKY)
                AlertDialog.Builder(requireContext())
                    .setItems(
                        arrayOf(
                            getString(if (isSticky) R.string.message_recent_un_pin else R.string.message_recent_pin),
                            getString(R.string.message_recent_delete)
                        )
                    ) { d, i ->
                        when (i) {
                            0 -> {
                                if (isSticky) {
                                    recentMsg.rawData.removeTag(TAG_STICKY)
                                } else {
                                    recentMsg.rawData.addTag(TAG_STICKY)
                                }
                                d.cancel()
                                IMService.updateRecentMessage(recentMsg)
                            }
                            1 -> {
                                IMService.deleteRecentMessage(recentMsg)
                                adapter.remove(position)
                                d.cancel()
                            }
                        }
                    }.create().show()
            }
            true
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
        adapter.setNewData(newData.map { it.rawData }.sortTo())
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