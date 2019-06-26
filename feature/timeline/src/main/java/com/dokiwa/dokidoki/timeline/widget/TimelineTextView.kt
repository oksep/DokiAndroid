package com.dokiwa.dokidoki.timeline.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.ui.ext.onceLayoutThen
import com.dokiwa.dokidoki.ui.ext.zoomTouchArea
import com.dokiwa.dokidoki.ui.view.ExpandableTextView

/**
 * Created by Septenary on 2019-06-23.
 */
class TimelineTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val expandableTextView: ExpandableTextView
    private val toggle: TextView

    init {
        View.inflate(context, R.layout.view_content_text, this)
        orientation = VERTICAL
        expandableTextView = this.findViewById(R.id.expandableTextView)
        toggle = this.findViewById(R.id.toggle)
        if (isInEditMode) {
            setText(this.expandableTextView.text?.toString())
        }
    }

    fun setText(text: String?) {
        if (text.isNullOrEmpty()) {
            this.visibility = View.GONE
        } else {
            this.visibility = View.VISIBLE
            this.expandableTextView.maxLines = 2
            this.expandableTextView.setText(text, toggle)
            this.toggle.onceLayoutThen {
                it.zoomTouchArea(this)
            }
        }
    }
}