package com.dokiwa.dokidoki.center.dialog

import android.app.Activity
import android.view.View
import com.dokiwa.dokidoki.center.R
import com.dokiwa.dokidoki.social.SocialHelper

class ShareDialog(activity: Activity, onShareClick: (SocialHelper.SocialType) -> Unit) : BottomDialog(activity) {

    init {
        setContentView(R.layout.ui_view_share)
        findViewById<View>(R.id.shareCancelBtn).setOnClickListener {
            cancel()
        }

        findViewById<View>(R.id.shareChannelQQ).setOnClickListener {
            onShareClick(SocialHelper.SocialType.QQ)
            cancel()
        }

        findViewById<View>(R.id.shareChannelWxFriend).setOnClickListener {
            onShareClick(SocialHelper.SocialType.WECHAT)
            cancel()
        }

        findViewById<View>(R.id.shareChannelWxCircle).setOnClickListener {
            onShareClick(SocialHelper.SocialType.WECHAT)
            cancel()
        }

        findViewById<View>(R.id.shareChannelWeibo).setOnClickListener {
            onShareClick(SocialHelper.SocialType.WEIBO)
            cancel()
        }
    }
}