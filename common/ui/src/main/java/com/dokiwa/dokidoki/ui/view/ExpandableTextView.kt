package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import at.blogc.android.views.ExpandableTextView
import com.dokiwa.dokidoki.ui.BuildConfig
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.ext.onceLayoutThen

/**
 * Created by Septenary on 2019-06-14.
 */
class ExpandableTextView : ExpandableTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setText(@StringRes resId: Int, onceLayout: (Boolean) -> Unit) {
        super.setText(resId)
        requestOnceLayout(onceLayout)
    }

    fun setText(text: CharSequence?, onceLayout: (Boolean) -> Unit) {
        super.setText(text)
        requestOnceLayout(onceLayout)
    }

    fun setText(@StringRes resId: Int, toggleView: TextView) {
        setText(resId) {
            setUpToggleView(it, toggleView)
        }
    }

    fun setText(text: CharSequence?, toggleView: TextView) {
        setText(text) {
            setUpToggleView(it, toggleView)
        }
    }

    private fun setUpToggleView(isEllipsize: Boolean, toggleView: TextView) {
        if (isEllipsize) {
            toggleView.visibility = View.VISIBLE
            toggleView.setText(R.string.ui_expandable_text_view_expand)
            toggleView.setOnClickListener {
                if (isExpanded) {
                    collapse()
                    toggleView.setText(R.string.ui_expandable_text_view_expand)
                } else {
                    expand()
                    toggleView.setText(R.string.ui_expandable_text_view_collapse)
                }
            }
        } else {
            toggleView.visibility = View.GONE
        }
    }

    private fun requestOnceLayout(onceLayout: (Boolean) -> Unit) {
        this.onceLayoutThen {
            if (maxLines > 0) {
                val isEllipsize = try {
                    layout.getEllipsisCount(maxLines - 1) > 0
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    } else {
                        e.printStackTrace()
                        true
                    }
                }
                onceLayout(isEllipsize)
            }
        }
    }
}