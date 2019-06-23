package com.dokiwa.dokidoki.message.home

import android.graphics.drawable.BitmapDrawable
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.social.SocialHelper
import kotlinx.android.synthetic.main.fragment_message.*

private const val KEY_VIEW_MODEL = 0x0003

class MessageFragment : BaseShareFragment(R.layout.fragment_message) {

    override fun onResume() {
        super.onResume()

        loginBtn1.setOnClickListener {
            shareImage(SocialHelper.SocialType.WECHAT)
        }
        loginBtn2.setOnClickListener {
            shareImage(SocialHelper.SocialType.QQ)
        }
        loginBtn3.setOnClickListener {
            shareImage(SocialHelper.SocialType.WEIBO)
        }

        toAdmin.setOnClickListener {
            openAdminActivity()
        }

        toLogin.setOnClickListener {
            openLoginActivity()
        }

        context?.let { context ->
            img2.let {
                //                val bmp = BitmapFactory.decodeResource(resources, R.drawable.test)
//                    .scaleByRatio(0.5f)
//                    .maskColor(context, R.color.white_20)
//                    .blurBitmap(context, 10f, true)
//                it.setImageDrawable(BitmapDrawable(resources, bmp))
            }
        }

        removeImg.loadImgFromNetWork("http://assets.septenary.cn/dirty_bytes.png")

        range_slider5.setOnThumbValueChangeListener { multiSlider, thumb, thumbIndex, value ->
            if (thumbIndex == 0) {
                // thumb1text.text = (value.toString())
            } else {
                // thumb2text.text = (value.toString())
            }
        }
    }

    private fun shareImage(type: SocialHelper.SocialType) {
        SocialHelper.shareImage(
            activity!!, type,
            (resources.getDrawable(R.drawable.share, null) as BitmapDrawable).bitmap
        ).subscribeApi(this)
    }

    private fun openAdminActivity() {
        context?.let {
            IAdminPlugin.get().launchAdmin(it)
        }
    }

    private fun openLoginActivity() {
        context?.let {
            ILoginPlugin.get().launchLoginActivity(it)
        }
    }
}
