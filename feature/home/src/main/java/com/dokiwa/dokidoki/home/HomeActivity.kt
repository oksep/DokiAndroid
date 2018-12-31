package com.dokiwa.dokidoki.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.home.fragment.FeedFragment
import com.dokiwa.dokidoki.home.fragment.MeFragment
import com.dokiwa.dokidoki.home.fragment.MsgFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Activity) {
            context.startActivity(
                Intent(context, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        toolBar.setRightIconClickListener(View.OnClickListener {
            // TODO: 2018/12/1 @Septenary 跳转到搜索页
            toast("to search page")
        })

        navigation.setOnNavigationItemSelectedListener(getNavigationSelectListener())

        viewPager.offscreenPageLimit = 2
        setupViewPager(viewPager)

        FeaturePlugin.get(IAdminPlugin::class.java).attachShakeAdmin(lifecycle)

        // TODO: 2018/12/28 @Septenary 测试未认证用户
        // Api.testUnAuth()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = BottomAdapter(supportFragmentManager)
        adapter.addFragment(FeedFragment.newInstance())
        adapter.addFragment(MsgFragment.newInstance())
        adapter.addFragment(MeFragment.newInstance())
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                navigation.menu.getItem(position).isChecked = true
                (adapter.getItem(position) as? OnPageSelectedListener)?.onPageSelected()
                toolBar.visibility = if (position == 2) View.GONE else View.VISIBLE
            }
        })
    }

    private fun getNavigationSelectListener(): BottomNavigationView.OnNavigationItemSelectedListener {
        return BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home_feed -> {
                    toolBar.setTitle(R.string.title_home_feed)
                    viewPager.setCurrentItem(0, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_home_msg -> {
                    toolBar.setTitle(R.string.title_home_msg)
                    viewPager.setCurrentItem(1, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_home_me -> {
                    toolBar.setTitle(R.string.title_home_me)
                    viewPager.setCurrentItem(2, false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
    }
}

interface OnPageSelectedListener {
    fun onPageSelected()
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