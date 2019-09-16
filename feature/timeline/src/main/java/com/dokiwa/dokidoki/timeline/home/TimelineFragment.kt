package com.dokiwa.dokidoki.timeline.home

import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.adapter.SimplePager2Adapter
import com.dokiwa.dokidoki.center.base.fragment.BaseShareFragment
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.timeline.api.TimelinePage
import com.dokiwa.dokidoki.timeline.create.CreateTimelineActivity
import com.dokiwa.dokidoki.timeline.notify.TimelineNotifyListActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_timeline.*

private const val KEY_VIEW_MODEL = 0x0002
private const val KEY_VIEW_MODEL_RECOMMEND = 0x0005
private const val KEY_VIEW_MODEL_FOLLOWING = 0x0006

class TimelineFragment : BaseShareFragment(R.layout.fragment_timeline) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = SimplePager2Adapter(requireFragmentManager(), lifecycle)

        pagerAdapter.addFragment(
            RecommendFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_KEY, KEY_VIEW_MODEL_RECOMMEND)
                }
            }
        )
        pagerAdapter.addFragment(
            FollowingFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_KEY, KEY_VIEW_MODEL_FOLLOWING)
                }
            }
        )

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
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
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
            CreateTimelineActivity.launch(requireActivity())
        }

        notifyBtn.setOnClickListener {
            TimelineNotifyListActivity.launch(requireContext())
        }
    }
}

internal class RecommendFragment : InnerPageFragment() {
    override fun onGetApiSingle(map: Map<String, String?>): Single<TimelinePage> {
        return Api.get(TimelineApi::class.java).getRecommendTimeline(map = map).flatMap { page ->
            IRelationshipPlugin.get()
                .toRelationStatusPair(Single.just(page.timelineList)) { it.user.id }
                .map { pairList ->
                    TimelinePage(
                        pairList.map { it.first.apply { relationStatus = it.second } },
                        page.next
                    )
                }
        }
    }
}

internal class FollowingFragment : InnerPageFragment() {
    override fun onGetApiSingle(map: Map<String, String?>): Single<TimelinePage> {
        return Api.get(TimelineApi::class.java).getFollowingTimeline(map = map)
    }
}