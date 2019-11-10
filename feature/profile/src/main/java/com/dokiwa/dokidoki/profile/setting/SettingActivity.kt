package com.dokiwa.dokidoki.profile.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
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
import com.dokiwa.dokidoki.social.SocialHelper
import com.dokiwa.dokidoki.social.socialgo.core.SocialGo
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

    private var wechatBound = false
    private var weiboBound = false
    private var qqBound = false

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

        clearCacheBtn.setOnClickListener {
            toast("TODO")
        }

        initSocialBtn()

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

    private fun initSocialBtn() {
        bindWechatBtn.setOnClickListener {
            if (wechatBound) {
                unbindSocial(SocialHelper.SocialType.WECHAT) {
                    wechatBound = false
                    initSocialBtnDesc(bindWechatBtnDesc, wechatBound)
                }
            } else {
                bindSocial(SocialHelper.SocialType.WECHAT) {
                    wechatBound = true
                    initSocialBtnDesc(bindWechatBtnDesc, wechatBound)
                }
            }
        }

        bindWeiboBtn.setOnClickListener {
            if (weiboBound) {
                unbindSocial(SocialHelper.SocialType.WEIBO) {
                    weiboBound = false
                    initSocialBtnDesc(bindWeiboBtnDesc, weiboBound)
                }
            } else {
                bindSocial(SocialHelper.SocialType.WEIBO) {
                    weiboBound = true
                    initSocialBtnDesc(bindWeiboBtnDesc, weiboBound)
                }
            }
        }

        bindQQBtn.setOnClickListener {
            if (qqBound) {
                unbindSocial(SocialHelper.SocialType.QQ) {
                    qqBound = false
                    initSocialBtnDesc(bindQQBtnDesc, qqBound)
                }
            } else {
                bindSocial(SocialHelper.SocialType.QQ) {
                    qqBound = true
                    initSocialBtnDesc(bindQQBtnDesc, qqBound)
                }
            }
        }
    }

    private fun initSocialBtnDesc(view: TextView, bound: Boolean?) {
        if (bound == true) {
            view.isEnabled = true
            view.setText(R.string.profile_setting_bound)
        } else {
            view.isEnabled = false
            view.setText(R.string.profile_setting_unbound)
        }
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
        Single.zip<SettingModel, Triple<Boolean, Boolean, Boolean>, PhoneBindModel, Triple<SettingModel, Triple<Boolean, Boolean, Boolean>, PhoneBindModel>>(
            Api.get(ProfileApi::class.java).getSettings(),
            ILoginPlugin.get().getSocialBindList(),
            Api.get(ProfileApi::class.java).getBindPhone(),
            Function3 { t1, t2, t3 ->
                Triple(t1, t2, t3)
            }
        ).subscribeApiWithDialog(this, this, {
            updateSettingModel(it.first)
            updateSocialBindModel(it.second)
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

    private fun updateSocialBindModel(socialListModel: Triple<Boolean, Boolean, Boolean>?) {
        wechatBound = socialListModel?.first ?: false
        weiboBound = socialListModel?.second ?: false
        qqBound = socialListModel?.third ?: false
        initSocialBtnDesc(bindWechatBtnDesc, wechatBound)
        initSocialBtnDesc(bindWeiboBtnDesc, weiboBound)
        initSocialBtnDesc(bindQQBtnDesc, qqBound)
    }

    private fun bindSocial(type: SocialHelper.SocialType, cb: () -> Unit) {
        ILoginPlugin.get().bindSocial(this, type).subscribeApiWithDialog(this, this, {
            toast(R.string.profile_setting_bind_success)
            cb()
        }, {
            toast(R.string.profile_setting_bind_failed)
        })
    }

    private fun unbindSocial(type: SocialHelper.SocialType, cb: () -> Unit) {
        ILoginPlugin.get().unbindSocial(type).subscribeApiWithDialog(this, this, {
            toast(R.string.profile_setting_unbind_success)
            cb()
        }, {
            toast(R.string.profile_setting_unbind_failed)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        SocialGo.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent) {
        SocialGo.onNewIntent(intent)
        super.onNewIntent(intent)
    }
}
