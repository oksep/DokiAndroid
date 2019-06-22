package com.dokiwa.dokidoki.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.home.pages.feed.FeedFragment
import com.dokiwa.dokidoki.home.pages.mine.MeFragment
import com.dokiwa.dokidoki.home.pages.msg.MsgFragment
import com.dokiwa.dokidoki.home.pages.timeline.TimelineFragment
import com.dokiwa.dokidoki.social.socialgo.core.SocialGo
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Activity) {
            context.startActivity(Intent(context, HomeActivity::class.java))
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initView()
        IAdminPlugin.get().attachShakeAdmin(lifecycle)
        ILoginPlugin.get().ensureLogin(this)
    }

    private fun initView() {
        homeViewPager.adapter = PageAdapter(supportFragmentManager)
        homeTabs.onTabClickListener = {
            homeViewPager.setCurrentItem(it, false)
        }
        homeTabs.setSelectedTab(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        SocialGo.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent) {
        SocialGo.onNewIntent(intent)
        super.onNewIntent(intent)
    }

    private inner class PageAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount() = 4

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> FeedFragment()
                1 -> TimelineFragment()
                2 -> MsgFragment()
                3 -> MeFragment()
                else -> throw IllegalStateException("out of index")
            }
        }
    }
}