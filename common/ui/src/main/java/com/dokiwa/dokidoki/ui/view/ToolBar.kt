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
    private val leftTextView by lazy { findViewById<TextView>(R.id.ui_left_text) }
    private val rightTextView by lazy { findViewById<TextView>(R.id.ui_right_text) }
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

        val leftText = a.getText(R.styleable.ToolBar_tb_left_text)
        val leftTextColor = a.getColor(R.styleable.ToolBar_tb_left_text_color, Color.WHITE)

        val rightText = a.getText(R.styleable.ToolBar_tb_right_text)
        val rightTextColor = a.getColor(R.styleable.ToolBar_tb_right_text_color, Color.WHITE)

        a.recycle()

        setLeftIcon(leftIcon)
        setRightIcon(rightIcon)

        setTitle(title)
        setTitleColor(titleColor)

        setLeftText(leftText)
        setLeftTextColor(leftTextColor)

        setRightText(rightText)
        setRightTextColor(rightTextColor)

        if (isInEditMode) {
            leftImgView.setImageResource(R.drawable.ic_back_dark)
            rightImgView.setImageResource(R.drawable.ic_search_dark)
            titleView.text = this@ToolBar::class.java.simpleName
        }

        fitsSystemWindows = true
    }

    fun setLeftIconClickListener(l: OnClickListener?): ToolBar {
        leftImgView.setOnClickListener(l)
        return this
    }

    fun setRightIconClickListener(l: OnClickListener?): ToolBar {
        rightImgView.setOnClickListener(l)
        return this
    }

    fun setLeftIcon(@DrawableRes id: Int): ToolBar {
        leftImgView.setImageResource(id)
        leftImgView.visibility = View.VISIBLE
        return this
    }

    fun setLeftIcon(d: Drawable?): ToolBar {
        leftImgView.setImageDrawable(d)
        leftImgView.visibility = if (d == null) View.GONE else View.VISIBLE
        return this
    }

    fun setRightIcon(@DrawableRes id: Int): ToolBar {
        rightImgView.setImageResource(id)
        rightImgView.visibility = View.VISIBLE
        return this
    }

    fun setRightIcon(d: Drawable?): ToolBar {
        rightImgView.setImageDrawable(d)
        rightImgView.visibility = if (d == null) View.GONE else View.VISIBLE
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

    fun setLeftText(@StringRes id: Int): ToolBar {
        leftTextView.visibility = View.VISIBLE
        leftTextView.setText(id)
        return this
    }

    fun setLeftText(text: CharSequence?): ToolBar {
        leftTextView.text = text
        leftTextView.visibility = if (text == null) View.GONE else View.VISIBLE
        return this
    }

    fun setLeftTextColor(color: Int): ToolBar {
        leftTextView.setTextColor(color)
        return this
    }

    fun setLeftTextClickListner(listener: OnClickListener?) {
        leftTextView.setOnClickListener(listener)
    }

    fun setRightText(@StringRes id: Int): ToolBar {
        rightTextView.visibility = View.VISIBLE
        rightTextView.setText(id)
        return this
    }

    fun setRightText(text: CharSequence?): ToolBar {
        rightTextView.text = text
        rightTextView.visibility = if (text == null) View.GONE else View.VISIBLE
        return this
    }

    fun setRightTextClickListner(listener: OnClickListener?) {
        rightTextView.setOnClickListener(listener)
    }

    fun setRightTextColor(color: Int): ToolBar {
        rightTextView.setTextColor(color)
        return this
    }
}
