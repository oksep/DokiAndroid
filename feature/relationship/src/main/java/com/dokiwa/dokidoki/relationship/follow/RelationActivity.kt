package com.dokiwa.dokidoki.relationship.follow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.base.adapter.SimplePager2Adapter
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.relationship.api.RelationApi
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_relationship.*

class RelationshipActivity : TranslucentActivity() {

    companion object {
        private const val EXTRA_IS_FOLLOWING = "extra.is_following"
        fun launch(context: Context, isFollowing: Boolean) {
            context.startActivity(
                Intent(context, RelationshipActivity::class.java).putExtra(
                    EXTRA_IS_FOLLOWING,
                    isFollowing
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relationship)

        viewPager.adapter = SimplePager2Adapter(supportFragmentManager, lifecycle).apply {
            addFragment(FollowingFragment())
            addFragment(FollowerFragment())
        }

        viewPager.offscreenPageLimit = 1

        TabLayoutMediator(tabLayout, viewPager, true,
            TabLayoutMediator.OnConfigureTabCallback { tab, position ->
                tab.text = if (position == 0) {
                    getString(R.string.relation_tab_following, 0)
                } else {
                    getString(R.string.relation_tab_follower, 0)
                }
            }
        ).attach()

        viewPager.post {
            viewPager.currentItem = if (intent.getBooleanExtra(EXTRA_IS_FOLLOWING, true)) 0 else 1
        }
    }
}

internal class FollowingFragment : PageFragment() {
    override fun onGetEmptyTip() = R.string.relation_following_empty
    override fun onGetApiSingle(): Single<List<UserProfile>> {
        return Api.get(RelationApi::class.java)
            .getFollowingList()
            .map { it.followingList ?: listOf() }
    }

    override fun updateIndicatorCountText(count: Int) {
        activity?.tabLayout?.getTabAt(0)?.text = getString(R.string.relation_tab_following, count)
    }
}

internal class FollowerFragment : PageFragment() {
    override fun onGetEmptyTip() = R.string.relation_follower_empty
    override fun onGetApiSingle(): Single<List<UserProfile>> {
        return Api.get(RelationApi::class.java)
            .getFollowerList()
            .map { it.followerList ?: listOf() }
    }

    override fun updateIndicatorCountText(count: Int) {
        activity?.tabLayout?.getTabAt(1)?.text = getString(R.string.relation_tab_follower, count)
    }
}
