package com.dokiwa.dokidoki.home.fragment

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.home.Log
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.social.SocialHelper
import com.dokiwa.dokidoki.ui.ext.blurBitmap
import com.dokiwa.dokidoki.ui.ext.maskColor
import com.dokiwa.dokidoki.ui.ext.scaleByRatio
import kotlinx.android.synthetic.main.fragment_msg.*

class MsgFragment : BaseFragment(), OnPageSelectedListener {

    companion object {
        fun newInstance() = MsgFragment()
    }

    private lateinit var viewModel: MsgViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_msg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginBtn1.setOnClickListener {
            shareImage(SocialHelper.SocialType.WECHAT)
        }
        loginBtn2.setOnClickListener {
            shareImage(SocialHelper.SocialType.QQ)
        }
        loginBtn3.setOnClickListener {
            shareImage(SocialHelper.SocialType.WEIBO)
        }

        viewModel = ViewModelProviders.of(this).get(MsgViewModel::class.java)

        toAdmin.setOnClickListener {
            openAdminActivity()
        }

        toLogin.setOnClickListener {
            openLoginActivity()
        }

        context?.let { context ->
            img2.let {
                val bmp = BitmapFactory.decodeResource(resources, R.drawable.test)
                    .scaleByRatio(0.5f)
                    .maskColor(context, R.color.white_20)
                    .blurBitmap(context, 10f, true)
                it.setImageDrawable(BitmapDrawable(resources, bmp))
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

    override fun onPageSelected() {
        Log.d("AAAAAA", "MsgFragment onPageSelected")
    }
}
