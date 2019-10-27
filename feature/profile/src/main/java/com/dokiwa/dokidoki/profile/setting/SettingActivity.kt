package com.dokiwa.dokidoki.profile.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.util.AppUtil
import com.dokiwa.dokidoki.profile.ProfileSP
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.PhoneBindModel
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.api.SettingModel
import com.dokiwa.dokidoki.profile.api.SocialListModel
import io.reactivex.Single
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_settings.*

private const val TAG = "SettingActivity"

class SettingActivity : TranslucentActivity() {

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    private var settingModel: SettingModel? = null
    private var phoneBindModel: PhoneBindModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        version.text = getString(
            R.string.profile_setting_version,
            AppUtil.getVerName(this),
            AppUtil.getVerCode(this)
        )

        notifySwitch.initSwitchBtn(ProfileSP.KEY_NOTIFY_ENABLE)
        soundSwitch.initSwitchBtn(ProfileSP.KEY_SOUND_ENABLE)
        vibrateSwitch.initSwitchBtn(ProfileSP.KEY_VIBRATE_ENABLE)

        logoutBtn.setOnClickListener {
            ILoginPlugin.get().logOut(this)
        }

        bindPhoneNumberBtn.setOnClickListener {
            alertChangeBindPhoneNum()
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

        initSettingSwitch()

        loadData()
    }

    private fun alertChangeBindPhoneNum() {
        val model = phoneBindModel ?: return
        AlertDialog.Builder(this)
            .setTitle(R.string.profile_setting_alert_title_change_phone_num)
            .setMessage(getString(R.string.profile_setting_alert_msg_change_phone_num, model.phone))
            .setNegativeButton(R.string.cancel) { d, _ ->
                d.cancel()
            }
            .setPositiveButton(R.string.confirm) { d, _ ->
                d.cancel()
                toast("TODO")
            }.create().show()
    }

    private fun Switch.initSwitchBtn(key: String) {
        isChecked = ProfileSP.getBoolean(key, false)
        setOnCheckedChangeListener { _, isChecked ->
            ProfileSP.save(key, isChecked)
        }
    }

    private fun initSettingSwitch() {
        feedRecommendSwitch.setOnCheckedChangeListener { _, isChecked ->
            Api.get(ProfileApi::class.java)
                .updateSettingAllowRecommend(isChecked)
                .subscribeApiWithDialog(this, this, ::updateSettingModel)
                {
                    toastApiException(it, R.string.center_toast_loading_failed_retry)
                    updateSettingModel(settingModel)
                }
        }

        realNameMsgSwitch.setOnCheckedChangeListener { _, isChecked ->
            Api.get(ProfileApi::class.java)
                .updateSettingRealNameMsg(isChecked)
                .subscribeApiWithDialog(this, this, ::updateSettingModel)
                {
                    toastApiException(it, R.string.center_toast_loading_failed_retry)
                    updateSettingModel(settingModel)
                }
        }
    }

    private fun loadData() {
        Single.zip<SettingModel, SocialListModel, PhoneBindModel, Triple<SettingModel, SocialListModel, PhoneBindModel>>(
            Api.get(ProfileApi::class.java).getSettings(),
            Api.get(ProfileApi::class.java).getSocialAccountList(),
            Api.get(ProfileApi::class.java).getBindPhone(),
            Function3 { t1, t2, t3 ->
                Triple(t1, t2, t3)
            }
        ).subscribeApiWithDialog(this, this, {
            updateSettingModel(it.first)
            updatePhoneBindModel(it.third)
        }, {
            toastApiException(it, R.string.center_toast_loading_failed_retry)
        })
    }

    private fun updateSettingModel(settingModel: SettingModel?) {
        this.settingModel = settingModel
        feedRecommendSwitch.setOnCheckedChangeListener(null)
        realNameMsgSwitch.setOnCheckedChangeListener(null)
        feedRecommendSwitch.isChecked = settingModel?.setting?.allowRecommend ?: false
        realNameMsgSwitch.isChecked = settingModel?.setting?.certificatedOnly ?: false
        initSettingSwitch()
    }

    private fun updatePhoneBindModel(phoneBindModel: PhoneBindModel) {
        bindPhoneNumberText.text = phoneBindModel.phone
        this.phoneBindModel = phoneBindModel
    }
}
