package com.dokiwa.dokidoki.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.login.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    showConfirmBtn()
                } else {
                    showThirdPartyLoginBtns()
                }
            }
        })

        loginByQQ.setOnClickListener {
            // TODO: 2018/12/2 @Septenary 通过 QQ 登录
        }

        loginByWechat.setOnClickListener {

        }

        loginByWeibo.setOnClickListener {

        }

        confirmBtn.setOnClickListener {
            if (editText.length() == 11) {
                VerifyCodeActivity.launch(this, "+86${editText.text}")
            } else {
                toast(R.string.login_input_correct_phone_number)
            }
        }

        showThirdPartyLoginBtns()

        editText.setText("15601919567")
    }

    private fun showThirdPartyLoginBtns() {
        confirmBtn.visibility = View.GONE
        loginByQQ.visibility = View.GONE
        loginByWechat.visibility = View.VISIBLE
        loginByWeibo.visibility = View.VISIBLE
        tip.text = getString(R.string.login_usage_third_party)
        tip.setOnClickListener(null)
    }

    private fun showConfirmBtn() {
        confirmBtn.visibility = View.VISIBLE
        loginByQQ.visibility = View.GONE
        loginByWechat.visibility = View.GONE
        loginByWeibo.visibility = View.GONE
        tip.setText(R.string.login_usage_protocol)
        tip.setOnClickListener {
            // TODO: 2018/12/3 @Septenary 跳转到 web 页面
        }
    }
}
