package com.dokiwa.dokidoki

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.ui.ext.blurBitmap
import com.dokiwa.dokidoki.ui.ext.maskColor
import com.dokiwa.dokidoki.ui.ext.scaleByRatio
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
