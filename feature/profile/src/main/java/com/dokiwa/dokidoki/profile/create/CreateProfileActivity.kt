package com.dokiwa.dokidoki.profile.create

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.dokiwa.dokidoki.center.base.activity.BaseChooseImageActivity
import com.dokiwa.dokidoki.center.base.adapter.SimplePagerAdapter
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.uploader.SimpleUploader
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.fragment.AvatarFragment
import com.dokiwa.dokidoki.profile.create.fragment.BaseStepFragment
import com.dokiwa.dokidoki.profile.create.fragment.BirthFragment
import com.dokiwa.dokidoki.profile.create.fragment.CityFragment
import com.dokiwa.dokidoki.profile.create.fragment.GenderFragment
import com.dokiwa.dokidoki.profile.create.fragment.HeightFragment
import com.dokiwa.dokidoki.profile.create.fragment.IStepFragmentInteract
import com.dokiwa.dokidoki.profile.create.fragment.NickFragment
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.activity_create_profile.*

/**
 * Created by Septenary on 2018/12/28.
 */
class CreateProfileActivity : BaseChooseImageActivity(), IStepFragmentInteract {

    companion object {
        private const val TAG = "CreateProfileActivity"

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

        sharedViewModel.gender.observe(this, Observer {
            Log.d(TAG, "gender $it")
        })
        sharedViewModel.birth.observe(this, Observer {
            Log.d(TAG, "birth $it")
        })
        sharedViewModel.height.observe(this, Observer {
            Log.d(TAG, "height $it")
        })
        sharedViewModel.city.observe(this, Observer {
            Log.d(TAG, "city $it")
        })
        sharedViewModel.nick.observe(this, Observer {
            Log.d(TAG, "nick $it")
        })
        sharedViewModel.avatar.observe(this, Observer {
            Log.d(TAG, "avatar $it")
        })

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
            addFragment(GenderFragment())
            addFragment(BirthFragment())
            addFragment(HeightFragment())
            addFragment(CityFragment())
            addFragment(NickFragment())
            addFragment(AvatarFragment())
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

        if (getCurrentStepFragment()?.skipable == true) {
            toolBar.showRightText()
        } else {
            toolBar.hideRightText()
        }
    }

    private fun getCurrentStepFragment(): BaseStepFragment? {
        return ((viewPager.adapter as? SimplePagerAdapter)?.getItem(viewPager.currentItem) as? BaseStepFragment)
    }

    // 上一步
    private fun preStep() {
        getCurrentStepFragment()?.reset()
        var index = viewPager.currentItem - 1
        if (index < 0) {
            index = 0
        }
        viewPager.currentItem = index
    }

    // 下一步
    private fun nextStep(isSkip: Boolean) {
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

    override fun requestPreStep() {
        preStep()
    }

    // 提交
    private fun submitPayload() {
        Log.d(TAG, "submit payload: $sharedViewModel")
//        finishAffinity()
//        Api.get(ProfileApi::class.java)
//            .createProfile(
//                sharedViewModel.gender.value,
//                sharedViewModel.birth.value,
//                sharedViewModel.height.value,
//                sharedViewModel.city.value,
//                sharedViewModel.nick.value
//            )
    }

    override fun onChooseImageFromAlbum(uri: Uri) {
        getCurrentStepFragment()?.onChooseImageFromAlbum(uri)
    }

    override fun onChooseImageFromCamera(uri: Uri) {
        getCurrentStepFragment()?.onChooseImageFromCamera(uri)
    }

    private fun uploadAvatar(uri: Uri) {
        SimpleUploader.uploadImage(uri, SimpleUploader.ImageType.AVATAR)
            .subscribeApiWithDialog(
                this,
                this,
                getString(R.string.profile_create_profile_avatar_upload_title),
                getString(R.string.profile_create_profile_avatar_upload_message),
                {
                    Log.d(TAG, "update success: $it")
                },
                {
                    Log.w(TAG, "update failed", it)
                }
            )
    }
}
