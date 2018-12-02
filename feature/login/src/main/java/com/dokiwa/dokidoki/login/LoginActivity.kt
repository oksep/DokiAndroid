package com.dokiwa.dokidoki.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : TranslucentActivity() {

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
            // TODO: 2018/12/2 @Septenary 
        }

        loginByWechat.setOnClickListener {

        }

        loginByWeibo.setOnClickListener {

        }

        confirmBtn.setOnClickListener {
            if (editText.length() == 11) {
                VerifyCodeActivity.launch(this, editText.text.toString())
            } else {
                toast(R.string.login_input_correct_phone_number)
            }
        }

        showThirdPartyLoginBtns()
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
            // TODO: 2018/12/3 @Septenary
        }
    }
}
