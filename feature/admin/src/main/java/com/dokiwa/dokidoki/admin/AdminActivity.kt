package com.dokiwa.dokidoki.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.admin.api.AdminApi
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toPrettyJson
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_admin.*


internal class AdminActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = Adapter().apply {
            addData(list)
        }
    }

    private class Adapter : BaseQuickAdapter<Item, BaseViewHolder>(android.R.layout.simple_list_item_1, null) {

        override fun convert(helper: BaseViewHolder, item: Item) {
            (helper.itemView as TextView).apply {
                text = item.text
                setOnClickListener {
                    item.clickListener.invoke(item.text)
                }
            }
        }
    }

    data class Item(val text: String, val clickListener: (String) -> Unit)

    private val list = listOf(
        Item("测试账号登陆") {
            // 12345000001 - 12345000010
            ILoginPlugin.get().loginTestingAccount(this, "+8612345000007", "233333")
        },
        Item("登出") {
            Api.testUnAuth()
        },
        Item("登出并登录") {
            ILoginPlugin.get().logOut(this)
        },
        Item("NIM 登录") {
            IMessagePlugin.get().loginNIM()
        },
        Item("NIM 登出") {
            IMessagePlugin.get().logoutNIM()
        },
        Item("绑定手机号页面") {
            ILoginPlugin.get().launchBindPhoneActivity(this)
        },
        Item("角色创建页面") {
            IProfilePlugin.get().launchCreateProfileActivity(this, null)
        },
        Item("API - 获取自己的资料") { text ->
            Api.get(AdminApi::class.java).getProfile().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取城市数据") { text ->
            Api.get(AdminApi::class.java).getCityConfig().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取行业数据") { text ->
            Api.get(AdminApi::class.java).getIndustryConfig().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取关键词数据") { text ->
            Api.get(AdminApi::class.java).getTagsConfig().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 第三方账号信息") { text ->
            Api.get(AdminApi::class.java).getThirdPartyInfo().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 第三方账号列表") { text ->
            Api.get(AdminApi::class.java).getThirdPartyList().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取绑定手机号") { text ->
            Api.get(AdminApi::class.java).getBindPhone().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取通知") { text ->
            Api.get(AdminApi::class.java).getNotification().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 用户配置项") { text ->
            Api.get(AdminApi::class.java).getUserConfig().subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取动态") { text ->
            Api.get(AdminApi::class.java).getTimeline("909").subscribeApi(this, {
                showApiContent(text, it)
            })
        },
        Item("API - 获取动态评论") { text ->
            Api.get(AdminApi::class.java).getTimelineComment("909").subscribeApi(this, {
                showApiContent(text, it)
            })
        }
    )

    private fun showApiContent(title: String, ele: JsonElement) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(
                LayoutInflater.from(this).inflate(R.layout.view_api_content, null).apply {
                    findViewById<TextView>(R.id.apiContent).text = ele.toPrettyJson()
                }
            )
            .setNegativeButton("Ok", null)
            .create()
            .show()
    }
}
