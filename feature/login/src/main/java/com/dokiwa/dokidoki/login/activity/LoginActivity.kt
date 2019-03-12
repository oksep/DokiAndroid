package com.dokiwa.dokidoki.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.api.exception.UnbindMobileNumberException
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.web.IWebPlugin
import com.dokiwa.dokidoki.login.Log
import com.dokiwa.dokidoki.login.R
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken
import com.dokiwa.dokidoki.social.SocialHelper
import com.dokiwa.dokidoki.social.socialgo.core.SocialGo
import com.dokiwa.dokidoki.ui.ext.hideSoftInputWhenClick
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"

class LoginActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initSocialView()
        showSocialLoginBtn()
    }

    private fun initView() {
        toolBar.leftIconView.setOnClickListener {
            finish()
        }
        content.hideSoftInputWhenClick()
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    showConfirmBtn()
                } else {
                    showSocialLoginBtn()
                }
            }
        })
    }

    private fun initSocialView() {
        loginByQQ.setOnClickListener {
            loginSocial(SocialHelper.SocialType.QQ)
        }
        loginByWechat.setOnClickListener {
            loginSocial(SocialHelper.SocialType.WECHAT)
        }
        loginByWeibo.setOnClickListener {
            loginSocial(SocialHelper.SocialType.WEIBO)
        }
        confirmBtn.setOnClickListener {
            if (editText.length() == 11) {
                VerifyCodeActivity.launch(this, "+86${editText.text}")
            } else {
                toast(R.string.login_input_correct_phone_number)
            }
        }
    }

    private fun showSocialLoginBtn() {
        confirmBtn.visibility = View.GONE
        loginByQQ.visibility = View.VISIBLE
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
            FeaturePlugin.get(IWebPlugin::class.java).launchWebActivity(this, "https://dokiwa.com/agreement/")
        }
    }

    private fun loginSocial(type: SocialHelper.SocialType) {
        val xSocialType = when (type) {
            SocialHelper.SocialType.QQ -> LoginApi.XSocialType.QQ
            SocialHelper.SocialType.WEIBO -> LoginApi.XSocialType.Weibo
            SocialHelper.SocialType.WECHAT -> LoginApi.XSocialType.Wechat
        }.type
        var socialCode: String? = null
        SocialHelper.auth(this, type)
            .flatMap {
                socialCode = it
                Api.get(LoginApi::class.java).loginBySocial(
                    socialCode = it,
                    socialType = xSocialType
                )
            }
            .subscribeApi(
                this,
                {
                    Log.d(TAG, "auth success: $it")
                    toHomePage(it)
                },
                {
                    Log.e(TAG, "auth error: $it")
                    if (it is UnbindMobileNumberException && socialCode != null) {
                        BindPhoneActivity.launch(this, socialCode!!, xSocialType)
                    } else {
                        toastApiException(it, R.string.login_failed)
                    }
                }
            )
    }

    // 登录成功后跳转到主页
    private fun toHomePage(userToken: UserToken) {
        ToHomeUtil.ensureProfileThenToHome(userToken, this)
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
