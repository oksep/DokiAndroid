package com.dokiwa.dokidoki.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.dokiwa.dokidoki.home.R

class HomeTabs : ConstraintLayout {

    var onTabClickListener: ((Int) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_tabs, this)
        children.forEachIndexed { index, view ->
            view.setOnClickListener {
                setSelectedTab(index)
            }
        }
    }

    fun setSelectedTab(selectedIndex: Int) {
        children.forEachIndexed { index, view ->
            view.isSelected = index == selectedIndex
        }
        onTabClickListener?.invoke(selectedIndex)
    }
}
