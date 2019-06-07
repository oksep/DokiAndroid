package com.dokiwa.dokidoki.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.home.fragment.FeedFragment
import com.dokiwa.dokidoki.home.fragment.MeFragment
import com.dokiwa.dokidoki.home.fragment.MsgFragment
import com.dokiwa.dokidoki.home.fragment.TimelineFragment
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

        setupViewPager()

        setupTabs()

        FeaturePlugin.get(IAdminPlugin::class.java).attachShakeAdmin(lifecycle)
        FeaturePlugin.get(ILoginPlugin::class.java).ensureLogin(this)
    }

    private fun setupViewPager() {
        homeViewPager.offscreenPageLimit = 3
        val adapter = PageAdapter(supportFragmentManager).apply {
            addFragment(FeedFragment.newInstance())
            addFragment(TimelineFragment.newInstance())
            addFragment(MsgFragment.newInstance())
            addFragment(MeFragment.newInstance())
        }
        homeViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                (adapter.getItem(position) as? OnPageSelectedListener)?.onPageSelected()
            }
        })
        homeViewPager.adapter = adapter
    }

    private fun setupTabs() {
        homeTabs.onTabClickListener = {
            val scrollToTop = homeViewPager.currentItem == it
            homeViewPager.setCurrentItem(it, false)
            if (scrollToTop) {
                ((homeViewPager.adapter as? PageAdapter)?.getItem(it) as? OnPageSelectedListener)?.requsetScrollContentToTop()
            }
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
}

interface OnPageSelectedListener {
    fun onPageSelected()
    fun requsetScrollContentToTop() {
    }
}

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments = mutableListOf<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }
}