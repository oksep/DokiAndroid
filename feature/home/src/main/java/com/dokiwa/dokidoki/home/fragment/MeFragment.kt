package com.dokiwa.dokidoki.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toPrettyJson
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.web.IWebPlugin
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.MeProfile
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
        loadData()
    }

    override fun onPageSelected() {
        context?.let {
             FeaturePlugin.get(IWebPlugin::class.java).launchWebActivity(it, "http://www.septenary.cn")
        }
    }

    private fun loadData() {
        Api.get(HomeApi::class.java)
            .getMeProfile()
            .subscribeApi(this, ::setProfile)
    }

    private fun setProfile(profile: MeProfile) {
        content.text = profile.toPrettyJson()
    }
}
