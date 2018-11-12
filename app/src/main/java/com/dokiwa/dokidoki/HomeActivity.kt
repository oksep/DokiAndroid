package com.dokiwa.dokidoki

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import com.dokiwa.dokidoki.center.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.ui.ext.blurBitmap
import com.dokiwa.dokidoki.ui.ext.maskColor
import com.dokiwa.dokidoki.ui.ext.scaleByRatio
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    companion object {
        fun launch(context: Activity) {
            context.startActivity(Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                toolBar.setTitle(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                toolBar.setTitle(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                toolBar.setTitle(R.string.title_notifications)
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

        img2.let {
            val bmp = BitmapFactory.decodeResource(resources, R.drawable.test)
                .scaleByRatio(0.5f)
                .maskColor(this, R.color.white_20)
                .blurBitmap(this, 10f, true)
            it.setImageDrawable(BitmapDrawable(resources, bmp))
        }
    }

    fun openAdminActivity(view: View) {
        IAdminPlugin.get().launchAdmin(this)
    }

    fun openLoginActivity(view: View) {
        ILoginPlugin.get().launchLoginActivity(this)
    }
}
