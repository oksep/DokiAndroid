package com.dokiwa.dokidoki.profile.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.base.adapter.SimplePagerAdapter
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.fragment.AvatarFragment
import com.dokiwa.dokidoki.profile.create.fragment.BaseStepFragment
import com.dokiwa.dokidoki.profile.create.fragment.BirthFragment
import com.dokiwa.dokidoki.profile.create.fragment.CityFragment
import com.dokiwa.dokidoki.profile.create.fragment.GenderFragment
import com.dokiwa.dokidoki.profile.create.fragment.IStepFragmentInteract
import com.dokiwa.dokidoki.profile.create.fragment.NickFragment
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.activity_create_profile.*

/**
 * Created by Septenary on 2018/12/28.
 */
private const val TAG = "CreateProfileActivity"

class CreateProfileActivity : TranslucentActivity(), IStepFragmentInteract {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, CreateProfileActivity::class.java))
        }
    }

    private val sharedViewModel by lazy {
        ViewModelProviders.of(this).get(SharedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        initView()
    }

    override fun onBackPressed() {
        preStep()
    }

    private fun initView() {
        toolBar.setRightTextClickListner(View.OnClickListener {
            nextStep(true)
        })
        val adapter = SimplePagerAdapter(supportFragmentManager).apply {
            addFragment(AvatarFragment())
            addFragment(BirthFragment())
            addFragment(CityFragment())
            addFragment(GenderFragment())
            addFragment(NickFragment())
        }
        viewPager.adapter = adapter
        val pagerListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                changeNavigation(position)
            }
        }
        viewPager.addOnPageChangeListener(pagerListener)
        pagerListener.onPageSelected(0)
    }

    private fun changeNavigation(position: Int) {
        if (position == 0) {
            toolBar.setLeftIcon(null)
            toolBar.setLeftIconClickListener(null)
        } else {
            toolBar.setLeftIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_back_light, null))
            toolBar.setLeftIconClickListener(View.OnClickListener {
                preStep()
            })
        }
    }

    // 上一步
    private fun preStep() {
        ((viewPager.adapter as? SimplePagerAdapter)?.getItem(viewPager.currentItem) as? BaseStepFragment)?.reset()
        var index = viewPager.currentItem - 1
        if (index < 0) {
            index = 0
        }
        viewPager.currentItem = index
    }

    // 下一步
    private fun nextStep(isSkip: Boolean) {
        // 跳过提交
        if (isSkip) {
            viewPager.currentItem = viewPager.currentItem + 1
            // submitPayload()
            return
        }

        // 最后一页提交
        if (viewPager.currentItem == viewPager.adapter!!.count - 1) {
            submitPayload()
            return
        }

        // 不是最后一页才切换
        viewPager.currentItem = viewPager.currentItem + 1
    }

    override fun requestNextStep() {
        nextStep(false)
    }

    // 提交
    private fun submitPayload() {
        // TODO: 2019/1/10 @Septenary 提交用户信息
        finishAffinity()
    }
}
