package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import com.dokiwa.dokidoki.ui.R

class ToolBar : FrameLayout {

    private val leftImgView by lazy { findViewById<AppCompatImageView>(R.id.ui_left_img) }
    private val rightImgView by lazy { findViewById<AppCompatImageView>(R.id.ui_right_img) }
    private val titleView by lazy { findViewById<TextView>(R.id.ui_title) }

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
        View.inflate(context, R.layout.ui_view_toolbar, this)

        val a = context.obtainStyledAttributes(attrs, R.styleable.ToolBar)
        val leftIcon = a.getDrawable(R.styleable.ToolBar_tb_left_ic)
        val rightIcon = a.getDrawable(R.styleable.ToolBar_tb_right_ic)
        val title = a.getString(R.styleable.ToolBar_tb_title)
        val titleColor = a.getColor(R.styleable.ToolBar_tb_title_color, Color.WHITE)
        a.recycle()

        setLeftIcon(leftIcon)
        setRightIcon(rightIcon)
        setTitle(title)
        setTitleColor(titleColor)

        if (isInEditMode) {
            leftImgView.setImageResource(R.drawable.ic_back_dark)
            rightImgView.setImageResource(R.drawable.ic_search_dark)
            titleView.text = this@ToolBar::class.java.simpleName
        }

        fitsSystemWindows = true
    }

    fun setLeftIconClickListener(l: OnClickListener): ToolBar {
        leftImgView.setOnClickListener(l)
        return this
    }

    fun setRightIconClickListener(l: OnClickListener): ToolBar {
        rightImgView.setOnClickListener(l)
        return this
    }

    fun setLeftIcon(@DrawableRes id: Int): ToolBar {
        leftImgView.setImageResource(id)
        return this
    }

    fun setLeftIcon(d: Drawable?): ToolBar {
        leftImgView.setImageDrawable(d)
        return this
    }

    fun setRightIcon(@DrawableRes id: Int): ToolBar {
        rightImgView.setImageResource(id)
        return this
    }

    fun setRightIcon(d: Drawable?): ToolBar {
        rightImgView.setImageDrawable(d)
        return this
    }

    fun setTitle(@StringRes id: Int): ToolBar {
        titleView.setText(id)
        return this
    }

    fun setTitle(title: String?): ToolBar {
        titleView.text = title
        return this
    }

    fun setTitleColor(color: Int): ToolBar {
        titleView.setTextColor(color)
        return this
    }
}
