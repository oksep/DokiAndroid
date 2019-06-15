package com.dokiwa.dokidoki.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.web.IWebPlugin
import com.dokiwa.dokidoki.login.R
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken
import com.dokiwa.dokidoki.ui.ext.hideSoftInputWhenClick
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_bind_phone.*
import java.util.concurrent.TimeUnit

private const val TAG = "BindPhoneActivity"

class BindPhoneActivity : TranslucentActivity() {

    private var phoneNumber: String? = null
    private var verifyCode: String? = null
    private val socialCode by lazy { intent.getStringExtra(EXTRA_SOCIAL_CODE) ?: "" }
    private val socialType by lazy { intent.getStringExtra(EXTRA_SOCIAL_TYPE) ?: "" }

    companion object {
        private const val EXTRA_SOCIAL_CODE = "extra.social_code"
        private const val EXTRA_SOCIAL_TYPE = "extra.social_type"
        fun launch(context: Context, socialCode: String, socialType: String) {
            context.startActivity(
                Intent(context, BindPhoneActivity::class.java)
                    .putExtra(EXTRA_SOCIAL_CODE, socialCode)
                    .putExtra(EXTRA_SOCIAL_TYPE, socialType)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_phone)
        content.hideSoftInputWhenClick()
        toolBar.leftIconView.setOnClickListener {
            finish()
        }
        countDownTip.setOnClickListener {
            requestVerifyCode()
        }
        phoneEditText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@BindPhoneActivity.phoneNumber = s?.toString()
            }
        })
        codeEditText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@BindPhoneActivity.verifyCode = s?.toString()
            }
        })
        confirmBtn.setOnClickListener {
            login()
        }
        tip.setText(R.string.login_usage_protocol)
        tip.setOnClickListener {
            FeaturePlugin.get(IWebPlugin::class.java).launchWebActivity(this, "https://dokiwa.com/agreement/")
        }
    }

    // 请求手机短信验证码
    private fun requestVerifyCode() {
        if (this.phoneNumber.isNullOrEmpty()) {
            toast(R.string.login_input_correct_phone_number)
            return
        }
        var countDown: Disposable? = null
        Api.get(LoginApi::class.java)
            .getVerifyCode("+86$phoneNumber")
            .doOnSubscribe {
                countDown = countDownTip()
                countDown?.bind(this@BindPhoneActivity)
            }
            .subscribeApi(
                this,
                {},
                {
                    countDown?.dispose()
                    toastApiException(it, R.string.log_request_verify_failed)
                }
            )
    }

    // 验证码倒计时
    private fun countDownTip(): Disposable {
        fun countDownComplete() {
            countDownTip.text = getString(R.string.log_verify_retry_simple)
            countDownTip.setOnClickListener {
                requestVerifyCode()
            }
        }

        val times = 60L

        return Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .doOnSubscribe {
                countDownTip.text = getString(R.string.log_verify_count_down_simple, times)
                countDownTip.setOnClickListener(null)
            }
            .doOnDispose {
                countDownComplete()
            }
            .take(times - 1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { countDownTip.text = getString(R.string.log_verify_count_down_simple, times - it - 1) },
                { countDownComplete() },
                { countDownComplete() }
            )
    }

    // 通过验证码登录
    private fun login() {

        if (this.phoneNumber.isNullOrEmpty()) {
            toast(R.string.login_input_correct_phone_number)
            return
        }

        if (this.verifyCode.isNullOrEmpty()) {
            toast(R.string.login_input_correct_verify_code)
            return
        }

        Api.get(LoginApi::class.java)
            .loginByVerifyCodeWithSocial(
                phoneNumber = "+86${this.phoneNumber}",
                verifyCode = this.verifyCode!!,
                socialType = socialType,
                socialCode = socialCode
            )
            .subscribeApi(this, ::toHomePage) { toastApiException(it, R.string.login_failed) }
    }

    // 登录成功后跳转到主页
    private fun toHomePage(userToken: UserToken) {
        ToHomeUtil.ensureProfileThenToHome(userToken, this)
    }
}
