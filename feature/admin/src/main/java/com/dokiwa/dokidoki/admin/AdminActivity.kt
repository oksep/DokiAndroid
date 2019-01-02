package com.dokiwa.dokidoki.admin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import kotlinx.android.synthetic.main.activity_admin.*

internal class AdminActivity : AppCompatActivity() {

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
                    item.clickListener.invoke()
                }
            }
        }
    }

    data class Item(val text: String, val clickListener: () -> Unit)

    private val list = listOf(
        Item("登录页面") {
            FeaturePlugin.get(ILoginPlugin::class.java).launchLoginActivity(this)
        }
    )
}
