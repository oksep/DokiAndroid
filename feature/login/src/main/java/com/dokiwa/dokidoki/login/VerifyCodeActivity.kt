package com.dokiwa.dokidoki.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.bind
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_verify_code.*
import java.util.concurrent.TimeUnit

private const val EXTRA_PHONE_NUMBER = "extra.phone_number"

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

        confirmBtn.setOnClickListener {
            // TODO: 2018/12/3 @Septenary 
        }

        requestVerifyCode()
    }

    private fun requestVerifyCode() {
        countDownTip()
    }

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
}
