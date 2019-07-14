package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import com.dokiwa.dokidoki.ui.util.ViewUtil

class HomeToolBar : AppToolBar {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        val top = if (isInEditMode) 0 else ViewUtil.getStatusBarHeight(context)
        setPadding(left, this.top + top, right, bottom)
        fitsSystemWindows = false
    }
}
