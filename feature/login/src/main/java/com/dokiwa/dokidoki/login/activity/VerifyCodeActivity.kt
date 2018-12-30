package com.dokiwa.dokidoki.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.dokiwa.dokidoki.login.Log
import com.dokiwa.dokidoki.login.LoginSP
import com.dokiwa.dokidoki.login.R
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.api.model.UserToken
import com.dokiwa.dokidoki.ui.ext.fadeInVisible
import com.dokiwa.dokidoki.ui.ext.fadeOutGone
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_verify_code.*
import java.util.concurrent.TimeUnit


private const val EXTRA_PHONE_NUMBER = "extra.phone_number"
private const val TAG = "VerifyCodeActivity"

class VerifyCodeActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context, phoneNumber: String) {
            context.startActivity(
                Intent(context, VerifyCodeActivity::class.java).putExtra(
                    EXTRA_PHONE_NUMBER,
                    phoneNumber
                )
            )
        }
    }

    private val phoneNumber by lazy { intent.getStringExtra(EXTRA_PHONE_NUMBER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_code)

        verifyCodeTitle.text = getString(R.string.login_verify_code_tip, phoneNumber)

        pinEntryEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val verifyCode = s.toString()
                if (verifyCode.length == 6) {
                    confirmBtn.fadeInVisible()
                } else {
                    confirmBtn.fadeOutGone()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        confirmBtn.setOnClickListener {
            login(pinEntryEdit.text.toString())
        }

        requestVerifyCode()
    }

    // 请求手机短信验证码
    private fun requestVerifyCode() {
        Api.get(LoginApi::class.java)
            .getVerifyCode(this.phoneNumber)
            .subscribeApi(
                this,
                {},
                {
                    Log.d(TAG, "Error $it")
                }
            )
        countDownTip()
    }

    // 验证码倒计时
    private fun countDownTip() {
        fun countDownComplete() {
            countDownTip.text = getString(R.string.log_verify_retry)
            countDownTip.setOnClickListener {
                requestVerifyCode()
            }
        }

        val times = 60L

        Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .doOnSubscribe {
                countDownTip.text = getString(R.string.log_verify_count_down, times)
                countDownTip.setOnClickListener(null)
            }
            .take(times - 1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { countDownTip.text = getString(R.string.log_verify_count_down, times - it - 1) },
                { countDownComplete() },
                { countDownComplete() }
            )
            .bind(this)
    }

    // 通过验证码登录
    private fun login(verifyCode: String) {
        Api.get(LoginApi::class.java)
            .loginByVerifyCode(this.phoneNumber, verifyCode)
            .subscribeApi(this, ::toHomePage)
    }

    // 登录成功后跳转到主页
    private fun toHomePage(userToken: UserToken) {
        LoginSP.saveUserToken(userToken)
        Api.resetAuthenticationToken(userToken.macKey, userToken.accessToken)
        FeaturePlugin.get(IHomePlugin::class.java).launchHomeActivity(this@VerifyCodeActivity)
    }
}
