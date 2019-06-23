package com.dokiwa.dokidoki.message

import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.message.home.MessageFragment

/**
 * Created by Septenary on 2018/10/24.
 */
class MessagePlugin : IMessagePlugin {
    override fun obtainHomeMessageFragment(): Fragment {
        return MessageFragment()
    }
}