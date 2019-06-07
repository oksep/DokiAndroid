package com.dokiwa.dokidoki.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.home.Log
import com.dokiwa.dokidoki.home.OnPageSelectedListener
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.HomeApi
import com.dokiwa.dokidoki.home.api.model.Feed
import com.dokiwa.dokidoki.home.api.model.FeedPage
import com.dokiwa.dokidoki.ui.view.LoadMoreView
import com.dokiwa.dokidoki.ui.view.RoundImageView
import kotlinx.android.synthetic.main.fragment_feed.*


class TimelineFragment : BaseFragment(), OnPageSelectedListener {
    override fun onPageSelected() {
    }

    companion object {
        fun newInstance() = TimelineFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }
}