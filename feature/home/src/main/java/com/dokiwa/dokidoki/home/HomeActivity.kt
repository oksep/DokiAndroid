package com.dokiwa.dokidoki.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.feed.IFeedPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.center.plugin.update.IUpdatePlugin
import com.dokiwa.dokidoki.social.socialgo.core.SocialGo
import kotlinx.android.synthetic.main.activity_home.*

private const val TAG = "HomeActivity"

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
        IUpdatePlugin.get().checkUpdate(this)
        IMessagePlugin.get().subscribeUnreadMsgCount().subscribe({
            homeTabs.setUnreadCount(it)
        }, {
            Log.e(TAG, "subscribeUnreadMsgCount failed", it)
        }).also { addDispose(it) }
    }

    private fun initView() {
        homeViewPager.isUserInputEnabled = false
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

    private inner class PageAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, lifecycle) {

        override fun getItemCount() = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> IFeedPlugin.get().obtainHomeFeedFragment()
                1 -> ITimelinePlugin.get().obtainHomeTimelineFragment()
                2 -> IMessagePlugin.get().obtainHomeMessageFragment()
                3 -> IProfilePlugin.get().obtainHomeMineFragment()
                else -> throw IllegalStateException("out of index")
            }
        }
    }
}