package com.dokiwa.dokidoki.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.home.fragment.FeedFragment
import com.dokiwa.dokidoki.home.fragment.MeFragment
import com.dokiwa.dokidoki.home.fragment.MsgFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity() {

    companion object {
        fun launch(context: Activity) {
            Log.d("HomeActivity", "launch")
            context.startActivity(Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home_feed -> {
                toolBar.setTitle(R.string.title_home_feed)
                viewPager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home_msg -> {
                toolBar.setTitle(R.string.title_home_msg)
                viewPager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home_me -> {
                toolBar.setTitle(R.string.title_home_me)
                viewPager.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        translucentStatusBar()

        setContentView(R.layout.activity_home)

        toolBar.setRightIconClickListener(View.OnClickListener {
            toast("Hello")
        })

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        setupViewPager(viewPager)

        FeaturePlugin.get(IAdminPlugin::class.java).attachShakeAdmin(lifecycle)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = BottomAdapter(supportFragmentManager)
        adapter.addFragment(FeedFragment.newInstance())
        adapter.addFragment(MsgFragment.newInstance())
        adapter.addFragment(MeFragment.newInstance())
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                navigation.menu.getItem(position).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }
}

class BottomAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
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