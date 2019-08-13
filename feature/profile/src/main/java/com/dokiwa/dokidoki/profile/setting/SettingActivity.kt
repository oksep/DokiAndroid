package com.dokiwa.dokidoki.profile.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.util.AppUtil
import com.dokiwa.dokidoki.profile.R
import kotlinx.android.synthetic.main.activity_settings.*

private const val TAG = "SettingActivity"

class SettingActivity : TranslucentActivity() {

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        version.text = getString(R.string.profile_setting_version, AppUtil.getVerName(this), AppUtil.getVerCode(this))
        logoutBtn.setOnClickListener {
            ILoginPlugin.get().logOut(this)
        }

        bindPhoneNumberBtn.setOnClickListener {
            toast("TODO")
        }

        bindWechatBtn.setOnClickListener {
            toast("TODO")
        }

        bindWeiboBtn.setOnClickListener {
            toast("TODO")
        }

        bindQQBtn.setOnClickListener {
            toast("TODO")
        }

        clearCacheBtn.setOnClickListener {
            toast("TODO")
        }
    }
}