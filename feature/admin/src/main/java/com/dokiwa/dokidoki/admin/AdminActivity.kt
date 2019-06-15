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
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
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
            FeaturePlugin.get(ILoginPlugin::class.java).loginTestingAccount(this, "+8612345000007", "233333")
        },
        Item("登出") {
            Api.testUnAuth()
        },
        Item("登出并登录") {
            FeaturePlugin.get(ILoginPlugin::class.java).logOut(this)
        },
        Item("绑定手机号页面") {
            FeaturePlugin.get(ILoginPlugin::class.java).launchBindPhoneActivity(this)
        },
        Item("角色创建页面") {
            FeaturePlugin.get(IProfilePlugin::class.java).launchCreateProfileActivity(this, null)
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
