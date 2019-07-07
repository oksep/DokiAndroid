package com.dokiwa.dokidoki.timeline.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.ui.view.ExpandableTextView

/**
 * Created by Septenary on 2019-06-23.
 */
class TimelineTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val expandableTextView: ExpandableTextView?
    private val toggleView: TextView?

    init {
        View.inflate(context, R.layout.view_content_text, this)
        orientation = VERTICAL
        expandableTextView = getChildAt(0) as? ExpandableTextView
        toggleView = getChildAt(1) as? TextView
    }

    fun setText(content: String?) {
        expandableTextView?.text = content
    }

    private fun setUpToggleView(isEllipsize: Boolean) {
        val toggleView = this.toggleView ?: return
        val expandableTextView = this.expandableTextView ?: return
        if (isEllipsize) {
            toggleView.visibility = View.VISIBLE
            toggleView.setText(R.string.ui_expandable_text_view_expand)
            toggleView.setOnClickListener {
                if (expandableTextView.isExpanded) {
                    expandableTextView.collapse()
                    toggleView.setText(R.string.ui_expandable_text_view_expand)
                } else {
                    expandableTextView.expand()
                    toggleView.setText(R.string.ui_expandable_text_view_collapse)
                }
            }
        } else {
            toggleView.visibility = View.GONE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val expandableTextView = this.expandableTextView ?: return
        if (expandableTextView.maxLines > 0) {
            val isEllipsize = try {
                expandableTextView.layout.getEllipsisCount(expandableTextView.layout.lineCount - 1) > 0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            setUpToggleView(isEllipsize)
        }
    }
}