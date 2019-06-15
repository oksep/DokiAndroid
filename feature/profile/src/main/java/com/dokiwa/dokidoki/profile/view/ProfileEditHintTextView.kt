package com.dokiwa.dokidoki.profile.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.dokiwa.dokidoki.profile.R

/**
 * Created by Septenary on 2019/2/12.
 */
class ProfileEditHintTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private var hintText: String? = null

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileEditHintTextView)
            hintText = a.getString(R.styleable.ProfileEditHintTextView_hint_text)
            a.recycle()
        }
        setText(null)
    }

    fun setText(text: String?) {
        if (text.isNullOrEmpty()) {
            setTextColor(ResourcesCompat.getColor(resources, R.color.profile_edit_text_hint, null))
            super.setText(hintText)
        } else {
            setTextColor(ResourcesCompat.getColor(resources, R.color.profile_edit_text, null))
            super.setText(text)
        }
    }
}