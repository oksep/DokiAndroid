package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.span.HtmlSpan

/**
 * Created by Septenary on 2019-06-11.
 */
class SpanTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpanTextView, defStyleAttr, 0)
        val spanText = typedArray.getString(R.styleable.SpanTextView_spanText)
        typedArray.recycle()
        spanText?.let {
            text = HtmlSpan.fromHtml(it)
        }
    }
}