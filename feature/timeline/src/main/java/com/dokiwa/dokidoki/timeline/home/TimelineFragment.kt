package com.dokiwa.dokidoki.timeline.home

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.base.adapter.SimplePager2Adapter
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.timeline.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_timeline.*

private const val KEY_VIEW_MODEL = 0x0002

class TimelineFragment : BaseShareFragment(R.layout.fragment_timeline) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = SimplePager2Adapter(requireFragmentManager(), lifecycle)

        pagerAdapter.addFragment(RecommendFragment())
        pagerAdapter.addFragment(FollowingFragment())

        viewPager.adapter = pagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }
        })

        TabLayoutMediator(tabLayout, viewPager, true,
            TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                tab.setText(
                    if (position == 0) {
                        R.string.timeline_home_tab_recommend
                    } else {
                        R.string.timeline_home_tab_follow
                    }
                )
            }
        ).attach()

        toolBar.rightIconView.setOnClickListener {
            requireContext().toast("TODO")
        }
    }
}