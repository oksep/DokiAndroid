package com.dokiwa.dokidoki.relationship.feedback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.relationship.R
import com.dokiwa.dokidoki.relationship.api.RelationApi
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.activity_feedback.*

private const val TAG = "FeedbackActivity"

class FeedbackActivity : TranslucentActivity() {

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, FeedbackActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        toolBar.rightTextView.setOnClickListener {
            Api.get(RelationApi::class.java).feedback(editText.text.toString()).subscribeApiWithDialog(
                this, this,
                {
                    toast("反馈成功")
                    finish()
                }, {
                    toast("反馈失败")
                }
            )
        }

        editText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                counts.text = editText.length().toString()
                if (editText.length() > 10) {
                    toolBar.rightTextView.alpha = 1f
                    toolBar.rightTextView.isEnabled = true
                } else {
                    toolBar.rightTextView.alpha = 0.75f
                    toolBar.rightTextView.isEnabled = false
                }
            }
        })

        editText.text = null
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
