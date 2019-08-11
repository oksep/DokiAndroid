package com.dokiwa.dokidoki.profile.certification

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.activity_certify_id.*
import java.net.URLEncoder

private const val TAG = "IdCertifyActivity"

class IdCertifyActivity : TranslucentActivity() {

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, IdCertifyActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certify_id)

        val textWatcher = object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                submitBtn.isEnabled = nameEditText.length() > 0 && idEditText.length() > 0
            }
        }

        nameEditText.addTextChangedListener(textWatcher)
        idEditText.addTextChangedListener(textWatcher)

        submitBtn.setOnClickListener {
            submitPayload()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent -> ${intent?.data}")
        if (intent?.data != null) {
            toast(R.string.profile_certify_id_certify_zhima_success)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun submitPayload() {
        val name = nameEditText.text.toString()
        val id = idEditText.text.toString()

        Log.d(TAG, "submitPayload -> name: $name, id: $id")

        Api.get(ProfileApi::class.java).updateCertifyId(name, id).subscribeApiWithDialog(this, this, {
            doVerify(it.zhimaCertifyUrl)
        }, {
            toastApiException(it, R.string.center_toast_loading_failed_retry)
        })
    }

    /**
     * 启动支付宝进行认证
     * ref: https://b.zmxy.com.cn/technology/openDoc.htm?relInfo=CERTIFICATION_QUICK_START
     * @param url 开放平台返回的URL
     */
    private fun doVerify(url: String) {
        if (hasApplication()) {
            val action = Intent(Intent.ACTION_VIEW)
            val builder = StringBuilder()
            builder.append("alipays://platformapi/startapp?appId=${BuildConfig.ZHIMA_APP_ID}&url=")
            builder.append(URLEncoder.encode(url))
            action.data = Uri.parse(builder.toString())
            startActivity(action)
            Log.d(TAG, "doVerify startActivity")
        } else {
            // 处理没有安装支付宝的情况
            AlertDialog.Builder(this)
                .setMessage("是否下载并安装支付宝完成认证?")
                .setPositiveButton("好的") { dialog, which ->
                    val action = Intent(Intent.ACTION_VIEW)
                    action.data = Uri.parse("https://m.alipay.com")
                    startActivity(action)
                }.setNegativeButton("算了") { dialog, which ->
                    dialog.dismiss()
                }.show()
            Log.d(TAG, "doVerify install alipay")
        }
    }

    /**
     * 判断是否安装了支付宝
     * @return true 为已经安装
     */
    private fun hasApplication(): Boolean {
        val manager = packageManager
        val action = Intent(Intent.ACTION_VIEW)
        action.data = Uri.parse("alipays://")
        val list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER)
        return list != null && list.size > 0
    }
}
