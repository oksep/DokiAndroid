package com.dokiwa.dokidoki.profile.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.util.AppUtil
import com.dokiwa.dokidoki.profile.ProfileSP
import com.dokiwa.dokidoki.profile.R
import kotlinx.android.synthetic.main.activity_settings.*

private const val TAG = "SettingActivity"

private const val SP_KEY = "key."

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

        notifySwitch.initSwitchBtn(ProfileSP.KEY_NOTIFY_ENABLE)
        soundSwitch.initSwitchBtn(ProfileSP.KEY_SOUND_ENABLE)
        vibrateSwitch.initSwitchBtn(ProfileSP.KEY_VIBRATE_ENABLE)

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

    private fun Switch.initSwitchBtn(key: String) {
        isChecked = ProfileSP.getBoolean(key, false)
        setOnCheckedChangeListener { _, isChecked ->
            ProfileSP.save(key, isChecked)
        }
    }
}
