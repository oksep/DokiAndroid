package com.dokiwa.dokidoki.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.util.ViewUtil

open class AppToolBar : ConstraintLayout {

    companion object {
        private const val VISIBLE = 0
        private const val INVISIBLE = 1
        private const val GONE = 2
    }

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

        val defaultTextSize = resources.getDimension(R.dimen.ui_toolbar_default_text_size)

        val a = context.obtainStyledAttributes(attrs, R.styleable.AppToolBar)

        val leftIconDrawable = a.getDrawable(R.styleable.AppToolBar_toolbar_leftIcon)
        val leftIconVisibility = a.getInt(R.styleable.AppToolBar_toolbar_leftIconVisibility, AppToolBar.VISIBLE)

        val rightIconDrawable = a.getDrawable(R.styleable.AppToolBar_toolbar_rightIcon)
        val rightIconVisibility = a.getInt(R.styleable.AppToolBar_toolbar_rightIconVisibility, AppToolBar.VISIBLE)

        val leftTextColor = a.getColor(R.styleable.AppToolBar_toolbar_leftTextColor, Color.WHITE)
        val leftText = a.getText(R.styleable.AppToolBar_toolbar_leftText)
        val leftTextPaddingH = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_leftTextPaddingH, 0)
        val leftTextPaddingV = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_leftTextPaddingV, 0)
        val leftTextSize = a.getDimension(R.styleable.AppToolBar_toolbar_leftTextSize, defaultTextSize)
        val leftTextVisibility = a.getInt(R.styleable.AppToolBar_toolbar_leftTextVisibility, AppToolBar.VISIBLE)

        val rightTextColor = a.getColor(R.styleable.AppToolBar_toolbar_rightTextColor, 0)
        val rightText = a.getText(R.styleable.AppToolBar_toolbar_rightText)
        val rightTextPaddingH = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_rightTextPaddingH, 0)
        val rightTextPaddingV = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_rightTextPaddingV, 0)
        val rightTextSize = a.getDimension(R.styleable.AppToolBar_toolbar_rightTextSize, defaultTextSize)
        val rightTextVisibility = a.getInt(R.styleable.AppToolBar_toolbar_rightTextVisibility, AppToolBar.VISIBLE)

        val titleTextColor = a.getColor(R.styleable.AppToolBar_toolbar_titleTextColor, 0)
        val titleText = a.getText(R.styleable.AppToolBar_toolbar_titleText)
        val titleTextPaddingH = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_titleTextPaddingH, 0)
        val titleTextPaddingV = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_titleTextPaddingV, 0)
        val titleTextSize = a.getDimension(R.styleable.AppToolBar_toolbar_titleTextSize, defaultTextSize)
        val titleVisibility = a.getInt(R.styleable.AppToolBar_toolbar_titleVisibility, AppToolBar.VISIBLE)

        val subTitleTextColor = a.getColor(R.styleable.AppToolBar_toolbar_subTitleTextColor, 0)
        val subTitleText = a.getText(R.styleable.AppToolBar_toolbar_subTitleText)
        val subTitleTextPaddingH = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_subTitleTextPaddingH, 0)
        val subTitleTextPaddingV = a.getDimensionPixelSize(R.styleable.AppToolBar_toolbar_subTitleTextPaddingV, 0)
        val subTitleTextSize = a.getDimension(R.styleable.AppToolBar_toolbar_subTitleTextSize, defaultTextSize)
        val subTitleVisibility = a.getInt(R.styleable.AppToolBar_toolbar_subTitleVisibility, AppToolBar.VISIBLE)

        a.recycle()

        val typedValue = TypedValue()
        if (!isInEditMode) {
            context.theme.resolveAttribute(
                R.attr.selectableItemBackgroundBorderless,
                typedValue,
                true
            )
        }
        val rippleBg = typedValue.resourceId

        val toolbarHeight = resources.getDimensionPixelSize(R.dimen.ui_toolbar_fit_height)

        // space
        addView(
            Space(context).apply {
                id = R.id.ui_toolbar_space
            },
            LayoutParams(0, toolbarHeight).apply {
                leftToLeft = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
            }
        )

        // left icon
        addView(
            newImageView(R.id.ui_toolbar_left_icon, leftIconDrawable, leftIconVisibility, rippleBg).apply {
                setOnClickListener {
                    (context as? Activity)?.onBackPressed()
                }
            },
            LayoutParams(0, 0).apply {
                leftToLeft = R.id.ui_toolbar_space
                topToTop = R.id.ui_toolbar_space
                bottomToBottom = R.id.ui_toolbar_space
                dimensionRatio = "H,1:1"
            }
        )

        // right icon
        addView(
            newImageView(R.id.ui_toolbar_right_icon, rightIconDrawable, rightIconVisibility, rippleBg),
            LayoutParams(0, 0).apply {
                rightToRight = R.id.ui_toolbar_space
                topToTop = R.id.ui_toolbar_space
                bottomToBottom = R.id.ui_toolbar_space
                dimensionRatio = "H,1:1"
            }
        )


        // left text
        addView(
            newTextView(
                R.id.ui_toolbar_left_text,
                leftText,
                leftTextColor,
                leftTextSize,
                leftTextPaddingH,
                leftTextPaddingV,
                leftTextVisibility,
                rippleBg
            ),
            LayoutParams(LayoutParams.WRAP_CONTENT, 0).apply {
                leftToRight = R.id.ui_toolbar_left_icon
                topToTop = R.id.ui_toolbar_space
                bottomToBottom = R.id.ui_toolbar_space
            }
        )

        // right text
        addView(
            newTextView(
                R.id.ui_toolbar_right_text,
                rightText,
                rightTextColor,
                rightTextSize,
                rightTextPaddingH,
                rightTextPaddingV,
                rightTextVisibility,
                rippleBg
            ),
            LayoutParams(LayoutParams.WRAP_CONTENT, 0).apply {
                rightToLeft = R.id.ui_toolbar_right_icon
                topToTop = R.id.ui_toolbar_space
                bottomToBottom = R.id.ui_toolbar_space
            }
        )

        // title
        addView(
            newTextView(
                R.id.ui_toolbar_title,
                titleText,
                titleTextColor,
                titleTextSize,
                titleTextPaddingH,
                titleTextPaddingV,
                titleVisibility,
                rippleBg
            ),
            LayoutParams(LayoutParams.WRAP_CONTENT, 0).apply {
                leftToRight = R.id.ui_toolbar_space
                rightToLeft = R.id.ui_toolbar_space
                topToTop = R.id.ui_toolbar_space
                bottomToTop = R.id.ui_toolbar_subtitle
            }
        )

        // subtitle
        addView(
            newTextView(
                R.id.ui_toolbar_subtitle,
                subTitleText,
                subTitleTextColor,
                subTitleTextSize,
                subTitleTextPaddingH,
                subTitleTextPaddingV,
                subTitleVisibility,
                rippleBg
            ),
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftToRight = R.id.ui_toolbar_space
                rightToLeft = R.id.ui_toolbar_space
                topToBottom = R.id.ui_toolbar_title
                bottomToBottom = R.id.ui_toolbar_space
            }
        )

        fitsSystemWindows = true
    }

    private fun newImageView(
        @IdRes id: Int,
        icon: Drawable?,
        visibility: Int,
        backgroundRes: Int
    ): ImageView {
        return AppCompatImageView(context).apply {
            this.id = id
            this.scaleType = ImageView.ScaleType.CENTER
            this.visibility = when (visibility) {
                AppToolBar.VISIBLE -> View.VISIBLE
                AppToolBar.INVISIBLE -> View.INVISIBLE
                AppToolBar.GONE -> View.GONE
                else -> View.VISIBLE
            }
            setBackgroundResource(backgroundRes)
            setImageDrawable(icon)
        }
    }

    private fun newTextView(
        @IdRes id: Int,
        text: CharSequence?,
        textColor: Int,
        textSize: Float,
        paddingHorizontal: Int,
        paddingVertical: Int,
        visibility: Int,
        backgroundRes: Int
    ): TextView {
        return TextView(context).apply {
            this.id = id
            this.maxLines = 1
            this.text = text
            this.setTextColor(textColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            setBackgroundResource(backgroundRes)
            if (isInEditMode) {
                setBackgroundColor(ViewUtil.randomColor())
            }
            this.gravity = Gravity.CENTER
            this.visibility = when (visibility) {
                AppToolBar.VISIBLE -> View.VISIBLE
                AppToolBar.INVISIBLE -> View.INVISIBLE
                AppToolBar.GONE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    val leftIconView: ImageView
        get() {
            return findViewById(R.id.ui_toolbar_left_icon)
        }

    val rightIconView: ImageView
        get() {
            return findViewById(R.id.ui_toolbar_right_icon)
        }

    val rightTextView: TextView
        get() {
            return findViewById(R.id.ui_toolbar_right_text)
        }

    val leftTextView: TextView
        get() {
            return findViewById(R.id.ui_toolbar_left_text)
        }

    val title: TextView
        get() {
            return findViewById(R.id.ui_toolbar_title)
        }

    val subTitle: TextView
        get() {
            return findViewById(R.id.ui_toolbar_subtitle)
        }
}
