package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.dokiwa.dokidoki.message.R

/**
 * Created by Septenary on 2019-07-15.
 * 输入控件 -> 表情贴图
 */
class InputEmoticonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.merge_input_panel_emoticon, this)
    }
}