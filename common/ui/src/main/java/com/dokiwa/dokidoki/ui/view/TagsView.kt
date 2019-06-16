package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.dokiwa.dokidoki.ui.R
import com.google.android.flexbox.FlexboxLayout

/**
 * Created by Septenary on 2019/2/12.
 */
class TagsView : FlexboxLayout {

    companion object {
        private const val SMALL = 0
        private const val LARGE = 1
    }

    private var size: Int = SMALL

    private var editable: Boolean = false

    private var defaultSelected: Boolean = true

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagsView, defStyle, 0)
        size = typedArray.getInt(R.styleable.TagsView_size, SMALL)
        editable = typedArray.getBoolean(R.styleable.TagsView_editable, false)
        defaultSelected = typedArray.getBoolean(R.styleable.TagsView_defaultSelected, true)
        typedArray.recycle()

        if (isInEditMode) {
            setTags(
                listOf(
                    "元气满满",
                    "第五人格",
                    "Cosplay",
                    "女装大佬",
                    "电影",
                    "欧洲人",
                    "萝莉音"
                )
            )
        }
    }

    private fun genTagView(tag: String, onTagClickCallback: ((String, View) -> Unit)?): TextView {
        val layoutRes = when (size) {
            LARGE -> R.layout.ui_view_tag_large
            else -> R.layout.ui_view_tag_small
        }
        val tagView = LayoutInflater.from(context).inflate(layoutRes, this, false) as TextView
        if (editable) {
            val closeIconRes = when (size) {
                LARGE -> R.drawable.ui_ic_tag_close_large
                else -> R.drawable.ui_ic_tag_close_small
            }
            tagView.setCompoundDrawablesWithIntrinsicBounds(0, 0, closeIconRes, 0)
            tagView.setOnClickListener {
                removeView(tagView)
                onTagsRemoveCallback?.invoke(tag)
            }
        } else {
            onTagClickCallback?.let {
                tagView.setOnClickListener { onTagClickCallback(tag, tagView) }
            }
        }
        tagView.tag = tag
        tagView.text = tag.trim()
        tagView.isSelected = defaultSelected
        addView(tagView)
        return tagView
    }

    fun setTags(tags: List<String>, onTagClickCallback: ((String, View) -> Unit)? = null) {
        removeAllViews()
        tags.forEach {
            genTagView(it, onTagClickCallback)
        }
    }

    var onTagsRemoveCallback: ((String) -> Unit)? = null
}