package com.dokiwa.dokidoki.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.bind
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.login.R
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken
import com.dokiwa.dokidoki.ui.ext.fadeInVisible
import com.dokiwa.dokidoki.ui.ext.fadeOutGone
import com.dokiwa.dokidoki.ui.ext.hideSoftInputWhenClick
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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

        content.hideSoftInputWhenClick()

        toolBar.setLeftIconClickListener(View.OnClickListener {
            finish()
        })

        requestVerifyCode()
    }

    // 请求手机短信验证码
    private fun requestVerifyCode() {
        var countDown: Disposable? = null
        Api.get(LoginApi::class.java)
            .getVerifyCode(this.phoneNumber)
            .doOnSubscribe {
                countDown = countDownTip()
                countDown?.bind(this@VerifyCodeActivity)
            }
            .subscribeApi(
                this,
                {},
                {
                    countDown?.dispose()
                    toastApiException(it, R.string.log_request_verify_failed)
                }
            )
        countDownTip()
    }

    // 验证码倒计时
    private fun countDownTip(): Disposable {
        fun countDownComplete() {
            countDownTip.text = getString(R.string.log_verify_retry)
            countDownTip.setOnClickListener {
                requestVerifyCode()
            }
        }

        val times = 60L

        return Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .doOnSubscribe {
                countDownTip.text = getString(R.string.log_verify_count_down, times)
                countDownTip.setOnClickListener(null)
            }
            .doOnDispose {
                countDownComplete()
            }
            .take(times - 1)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { countDownTip.text = getString(R.string.log_verify_count_down, times - it - 1) },
                { countDownComplete() },
                { countDownComplete() }
            )
    }

    // 通过验证码登录
    private fun login(verifyCode: String) {
        Api.get(LoginApi::class.java)
            .loginByVerifyCode(this.phoneNumber, verifyCode)
            .subscribeApi(this, ::toHomePage)
    }

    // 登录成功后跳转到主页
    private fun toHomePage(userToken: UserToken) {
        ToHomeUtil.ensureProfileThenToHome(userToken, this)
    }
}
