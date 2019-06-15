package com.dokiwa.dokidoki.profile.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.activity_profile_edit_intro.*

private const val TAG = "IntroEditActivity"

class IntroEditActivity : TranslucentActivity() {

    companion object {

        const val EXTRA_INTRO = "extra.user.intro"

        const val REQUEST_CODE = 0x0010

        fun launch(context: Activity, intro: String?) {
            context.startActivityForResult(
                Intent(context, IntroEditActivity::class.java).putExtra(EXTRA_INTRO, intro), REQUEST_CODE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit_intro)

        toolBar.rightTextView.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_INTRO, introEditText.text.toString()))
            finish()
        }

        val intro = intent.getStringExtra(EXTRA_INTRO)

        introEditText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                counts.text = introEditText.length().toString()
            }
        })

        introEditText.setText(intro)
        introEditText.setSelection(introEditText.length())
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

}
