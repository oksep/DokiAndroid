package com.dokiwa.dokidoki.profile.create

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseChooseImageActivity
import com.dokiwa.dokidoki.center.base.adapter.SimplePagerAdapter
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.uploader.SimpleUploader
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.ProfileSP
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.create.fragment.AvatarFragment
import com.dokiwa.dokidoki.profile.create.fragment.BaseStepFragment
import com.dokiwa.dokidoki.profile.create.fragment.BirthFragment
import com.dokiwa.dokidoki.profile.create.fragment.CityFragment
import com.dokiwa.dokidoki.profile.create.fragment.GenderFragment
import com.dokiwa.dokidoki.profile.create.fragment.HeightFragment
import com.dokiwa.dokidoki.profile.create.fragment.IStepFragmentInteract
import com.dokiwa.dokidoki.profile.create.fragment.NickFragment
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_create_profile.*

/**
 * Created by Septenary on 2018/12/28.
 */
class CreateProfileActivity : BaseChooseImageActivity(), IStepFragmentInteract {

    companion object {
        private const val TAG = "CreateProfileActivity"

        private const val EXTRA_USER_TOKEN = "extra.user_token"

        fun launch(context: Context, userToken: Parcelable?) {
            context.startActivity(
                Intent(context, CreateProfileActivity::class.java).putExtra(EXTRA_USER_TOKEN, userToken)
            )
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
        toolBar.rightTextView.setOnClickListener {
            nextStep(true)
        }
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
            toolBar.leftIconView.setImageDrawable(null)
            toolBar.leftIconView.setOnClickListener(null)
        } else {
            toolBar.leftIconView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ui_ic_back_light, null))
            toolBar.leftIconView.setOnClickListener {
                preStep()
            }
        }

        if (getCurrentStepFragment()?.skipable == true) {
            toolBar.rightTextView.visibility = View.VISIBLE
        } else {
            toolBar.rightTextView.visibility = View.GONE
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

        val uri = sharedViewModel.avatar.value
        if (uri != null) {
            SimpleUploader.uploadImage(uri, SimpleUploader.ImageType.AVATAR)
        } else {
            Single.just(SimpleUploader.UploadImageResult.obtainEmptyImageResult())
        }.flatMap {
            Api.get(ProfileApi::class.java)
                .createProfile(
                    sharedViewModel.gender.value,
                    sharedViewModel.birth.value,
                    sharedViewModel.height.value,
                    sharedViewModel.city.value,
                    sharedViewModel.nick.value,
                    it.image.rawUrl
                )
        }.subscribeApiWithDialog(this, this,
            {
                Log.d(TAG, "submit payload success")
                FeaturePlugin.get(ILoginPlugin::class.java)
                    .saveLoginUserToken(intent.getParcelableExtra(CreateProfileActivity.EXTRA_USER_TOKEN))
                FeaturePlugin.get(IHomePlugin::class.java).launchHomeActivity(this)
                ProfileSP.saveLoginUserProfile(it.profile)
                finishAffinity()
            },
            {
                Log.e(TAG, "submit payload failed", it)
                toast(R.string.loading_failed_retry)
            }
        )
    }

    override fun onChooseImageFromAlbum(uri: Uri) {
        getCurrentStepFragment()?.onChooseImageFromAlbum(uri)
    }

    override fun onChooseImageFromCamera(uri: Uri) {
        getCurrentStepFragment()?.onChooseImageFromCamera(uri)
    }
}
