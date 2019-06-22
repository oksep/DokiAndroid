package com.dokiwa.dokidoki.home.pages.timeline

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.base.adapter.SimplePager2Adapter
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.pages.BasePageFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : BasePageFragment(R.layout.fragment_timeline) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = SimplePager2Adapter(requireFragmentManager(), lifecycle)

        pagerAdapter.addFragment(RecommendFragment())
        pagerAdapter.addFragment(FollowFragment())

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
                tab.setText(if (position == 0) R.string.home_timeline_recommend else R.string.home_timeline_follow)
            }
        ).attach()

        toolBar.rightIconView.setOnClickListener {
            requireContext().toast("TODO")
        }
    }
}

internal class RecommendFragment : Fragment(R.layout.fragment_timeline_recommend) {

}

internal class FollowFragment : Fragment(R.layout.fragment_timeline_follow) {

}