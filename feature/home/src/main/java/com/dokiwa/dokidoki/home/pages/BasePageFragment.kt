package com.dokiwa.dokidoki.home.pages

import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.base.fragment.BaseFragment

/**
 * Created by Septenary on 2019-06-12.
 */
abstract class BasePageFragment(@LayoutRes contentLayoutId: Int) : BaseFragment(contentLayoutId) {
//    protected val sharedViewModel by lazy {
//        ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
//    }
}