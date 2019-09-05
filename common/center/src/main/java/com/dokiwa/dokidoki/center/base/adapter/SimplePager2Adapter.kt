package com.dokiwa.dokidoki.center.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Septenary on 2019/1/2.
 */
class SimplePager2Adapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    private val fragments = mutableListOf<Fragment>()

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment): SimplePager2Adapter {
        fragments.add(fragment)
        return this
    }
}