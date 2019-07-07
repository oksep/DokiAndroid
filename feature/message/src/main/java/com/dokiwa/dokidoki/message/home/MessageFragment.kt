package com.dokiwa.dokidoki.message.home

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.message.MessagePlugin
import com.dokiwa.dokidoki.message.R
import kotlinx.android.synthetic.main.fragment_message.*

private const val KEY_VIEW_MODEL = 0x0003

class MessageFragment : BaseShareFragment(R.layout.fragment_message) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testBtn.setOnClickListener {
            context?.toast("wowowo")
            val loginInfo = (IMessagePlugin.get() as MessagePlugin).loginInfo()
            println(loginInfo)
        }
    }
}
