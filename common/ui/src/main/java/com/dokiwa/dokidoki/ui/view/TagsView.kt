package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.dokiwa.dokidoki.ui.R
import com.google.android.flexbox.FlexboxLayout

/**
 * Created by Septenary on 2019/2/12.
 */
class TagsView : FlexboxLayout {

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

    fun setTags(tags: List<String>) {
        removeAllViews()
        repeat(tags.size) {
            LayoutInflater.from(context).inflate(R.layout.ui_view_tag, this, true)
        }
        tags.forEachIndexed { index, tag ->
            (getChildAt(index) as? TextView)?.text = tag.trim()
        }
    }

}