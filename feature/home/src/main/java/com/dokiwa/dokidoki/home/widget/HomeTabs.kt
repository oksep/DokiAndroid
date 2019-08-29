package com.dokiwa.dokidoki.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.dokiwa.dokidoki.home.R
import kotlinx.android.synthetic.main.view_home_tabs.view.*

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

    fun setUnreadCount(count: Int) {
        if (count > 0) {
            homeTagMsgUnreadCount.visibility = View.VISIBLE
            homeTagMsgUnreadCount.text = if (count > 99) "99+" else count.toString()
        } else {
            homeTagMsgUnreadCount.visibility = View.GONE
        }
    }
}
