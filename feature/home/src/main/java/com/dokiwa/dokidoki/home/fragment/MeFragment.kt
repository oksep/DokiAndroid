package com.dokiwa.dokidoki.home.fragment

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toPrettyJson
import com.dokiwa.dokidoki.home.Log
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.MeProfile
import com.dokiwa.dokidoki.social.SocialHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : BaseFragment(), OnPageSelectedListener {

    companion object {
        fun newInstance() = MeFragment()
    }

    private val sharedModel by lazy {
        ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
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
    }

    override fun onPageSelected() {
        context?.let {
            // FeaturePlugin.get(IWebPlugin::class.java).launchWebActivity(it, "http://www.septenary.cn")
        }

    }

    fun login(type: SocialHelper.SocialType) {
        SocialHelper.login(activity!!, type)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeApi(this, {
                text.text = it.toPrettyJson()
                Log.d("MicroMsg.SDK.$type", "auth success: $it")
            }, {
                text.text = "error: $it"
                Log.e("MicroMsg.SDK.$type", "auth error: $it")
            })
    }

    fun shareImage(type: SocialHelper.SocialType) {
        SocialHelper.shareImage(
            activity!!, type,
            (resources.getDrawable(R.drawable.share, null) as BitmapDrawable).bitmap
        )
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeApi(this, {
                text.text = it.toPrettyJson()
                Log.d("MicroMsg.SDK.$type", "分享 success: $it")
            }, {
                text.text = "error: $it"
                Log.e("MicroMsg.SDK.$type", "分享 error: $it")
            })
    }

    private fun loadData() {
        Api.get(HomeApi::class.java)
            .getMeProfile()
            .subscribeApi(this, ::setProfile)
    }

    private fun setProfile(profile: MeProfile) {
        // text = profile.toPrettyJson()
    }
}
