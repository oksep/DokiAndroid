package com.dokiwa.dokidoki.relationship.blacklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.view.children
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.relationship.Log
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.relationship.api.RelationApi
import com.dokiwa.dokidoki.relationship.api.ReportTypeList
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_ban_report.*

private const val TAG = "BanReportActivity"

class BanReportActivity : TranslucentActivity() {

    enum class ObjectType(val value: Int) { USER(0), TIMELINE(1), COMMENT(2) }

    companion object {

        private const val EXTRA_USER_UUID = "extra.user_uuid"
        private const val EXTRA_REPORT_ID = "extra.report_id"
        private const val EXTRA_OBJECT_TYPE = "extra.object_type"

        fun launch(context: Context, userUUID: String, reportId: String, type: ObjectType) {
            context.startActivity(
                Intent(context, BanReportActivity::class.java)
                    .putExtra(EXTRA_USER_UUID, userUUID)
                    .putExtra(EXTRA_REPORT_ID, reportId)
                    .putExtra(EXTRA_OBJECT_TYPE, type.value)
            )
        }
    }

    private var userProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ban_report)

        editText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                counts.text = editText.length().toString()
            }
        })

        editText.text = null

        submitBtn.setOnClickListener {
            val tag = radioGroup.findViewById<View>(radioGroup.checkedRadioButtonId)?.tag
            if (tag is ReportTypeList.ReportType) {
                submitPayload(tag)
            } else {
                toast(R.string.relation_ban_report_no_reason)
            }
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            submitBtn.isEnabled = true
        }

        loadData(Api.get(RelationApi::class.java).getReportTypeList(), true)

        loadUserData()
    }

    private fun submitPayload(reportType: ReportTypeList.ReportType) {
        val objType = intent.getIntExtra(EXTRA_OBJECT_TYPE, -1)
        if (objType == -1) {
            return Log.w(TAG, "[submitPayload] illegal obj type -> $objType")
        }

        val objId = if (objType == ObjectType.USER.value) {
            userProfile?.userId ?: return Log.w(TAG, "[submitPayload] userProfile is not available")
        } else {
            val id = intent.getIntExtra(EXTRA_REPORT_ID, -1)
            if (id == -1) {
                return Log.w(TAG, "[submitPayload] illegal obj id")
            } else {
                id
            }
        }

        Api.get(RelationApi::class.java)
            .createReport(
                obj = objType,
                objId = objId,
                type = reportType.type,
                reason = editText.text.toString()
            )
            .subscribeApiWithDialog(this, this, {
                toast(R.string.relation_ban_report_success)
                finish()
            }, {
                toastApiException(it, R.string.relation_ban_report_failed)
            })
    }

    private fun loadUserData() {
        IProfilePlugin.get()
            .getUserProfile(intent.getStringExtra(EXTRA_USER_UUID))
            .subscribeApi(this, {
                userProfile = it.profile
                avatar.glideAvatar(it.profile.avatar)
                name.text = it.profile.nickname
            })
    }

    private fun loadData(single: Single<ReportTypeList>, retry: Boolean) {
        single.subscribeApi(this, ::setUp) {
            if (retry) {
                loadData(Api.getLocalAsset(this, RelationApi::class.java).getLocalReportTypeList(), false)
            } else {
                Log.e(TAG, "get report type list failed", it)
            }
        }
    }

    private fun setUp(data: ReportTypeList) {
        data.typeList.forEach { _ ->
            View.inflate(this, R.layout.view_item_report_reason, radioGroup)
        }

        radioGroup.children.forEachIndexed { index, view ->
            data.typeList.getOrNull(index)?.let {
                (view as? RadioButton)?.text = it.text
                view.tag = it
            }
        }
    }
}
