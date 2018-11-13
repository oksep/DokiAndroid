package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Created by Septenary on 2018/11/14.
 */
class SimpleBottomNavigationView : BottomNavigationView {

    private var preSize = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        this.resetIconSize()
    }

    private fun resetIconSize() {
        val size = measuredHeight
        if (preSize != size) {
            (getChildAt(0) as? BottomNavigationMenuView)?.let { menuView ->
                for (i in 0 until menuView.childCount) {
                    menuView.getChildAt(i)?.findViewById<View>(com.google.android.material.R.id.icon)?.let { iconView ->
                        iconView.layoutParams.let {
                            it.width = size
                            it.height = size
                        }
                    }
                }
            }
            preSize = size
        }
    }
}