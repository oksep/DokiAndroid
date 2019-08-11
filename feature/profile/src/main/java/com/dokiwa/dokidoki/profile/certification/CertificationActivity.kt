package com.dokiwa.dokidoki.profile.certification

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.CertificationWrap
import com.dokiwa.dokidoki.profile.api.ProfileApi
import kotlinx.android.synthetic.main.activity_certification.*

private const val TAG = "CertificationActivity"

class CertificationActivity : TranslucentActivity() {

    companion object {

        const val CERTIFY_STATUS_UNKNOWN = 0 // 未认证
        const val CERTIFY_STATUS_ING = 1 // 认证中
        const val CERTIFY_STATUS_SUCCESS = 2 // 认证成功
        const val CERTIFY_STATUS_FAILED = 3 // 认证失败

        fun launch(context: Context) {
            context.startActivity(Intent(context, CertificationActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certification)
        loadData()
    }

    private fun loadData() {
        Api.get(ProfileApi::class.java).getCertification().subscribeApi(this, ::setData) {
            Log.e(TAG, "load certification failed", it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: CertificationWrap) {
        Log.d(TAG, "load certification -> $data")
        val certification = data.certification
        when {
            certification.identification?.status == CERTIFY_STATUS_SUCCESS -> {
                val identification = certification.identification
                identifyCertifyDesc.text = "${identification?.name}\n${identification?.number}"
                identifyCertifyDesc.setTextColor(ContextCompat.getColor(this, R.color.text_dft))
                identifyCertifyBtn.visibility = View.GONE
            }
            certification.identification?.status == CERTIFY_STATUS_ING -> {
                identifyCertifyDesc.setText(R.string.profile_certify_ing)
                identifyCertifyDesc.setTextColor(ContextCompat.getColor(this, R.color.dd_red))
                identifyCertifyBtn.visibility = View.GONE
            }
            else -> {
                identifyCertifyDesc.setTextColor(ContextCompat.getColor(this, R.color.text_tip))
                identifyCertifyBtn.visibility = View.VISIBLE
                identifyCertifyBtn.setOnClickListener {
                    IdCertifyActivity.launch(this)
                }
            }
        }

        when {
            certification.education?.status == CERTIFY_STATUS_SUCCESS -> {
                val education = certification.education
                eduCertifyDesc.text = "${education?.education}\n${education?.school}"
                eduCertifyDesc.setTextColor(ContextCompat.getColor(this, R.color.text_dft))
                eduCertifyBtn.visibility = View.GONE
            }
            certification.identification?.status == CERTIFY_STATUS_ING -> {
                eduCertifyDesc.setText(R.string.profile_certify_ing)
                eduCertifyDesc.setTextColor(ContextCompat.getColor(this, R.color.dd_red))
                eduCertifyBtn.visibility = View.GONE
            }
            else -> {
                eduCertifyDesc.setTextColor(ContextCompat.getColor(this, R.color.text_tip))
                eduCertifyBtn.visibility = View.VISIBLE
                eduCertifyBtn.setOnClickListener {
                    if (certification.identification?.status != CERTIFY_STATUS_SUCCESS) {
                        toast(R.string.profile_certify_identify_first)
                    } else {
                        EduCertifyActivity.launch(this)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IdCertifyActivity.REQUEST_CODE, EduCertifyActivity.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    loadData()
                }
            }
        }
    }
}
