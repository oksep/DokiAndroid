package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.util.AttributeSet
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.ext.dp2pxExact
import com.makeramen.roundedimageview.Corner
import com.makeramen.roundedimageview.RoundedImageView

class RoundImageView : RoundedImageView {


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

        var radius = context.dp2pxExact(6f)
        var isCircle = false
        var borderWidth = 0.toFloat()
        var borderColor = 0
        var radiusTopLeft = -1f
        var radiusTopRight = -1f
        var radiusBottomLeft = -1f
        var radiusBottomRight = -1f

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
            radius = a.getDimension(R.styleable.RoundImageView_rv_radius, -1f)
            isCircle = a.getBoolean(R.styleable.RoundImageView_rv_circle, false)
            borderWidth = a.getDimension(R.styleable.RoundImageView_rv_border_width, -1f)
            borderColor = a.getColor(R.styleable.RoundImageView_rv_border_color, 0)
            radiusTopLeft = a.getDimension(R.styleable.RoundImageView_rv_radius_top_left, -1f)
            radiusTopRight = a.getDimension(R.styleable.RoundImageView_rv_radius_top_right, -1f)
            radiusBottomLeft = a.getDimension(R.styleable.RoundImageView_rv_radius_bottom_left, -1f)
            radiusBottomRight = a.getDimension(R.styleable.RoundImageView_rv_radius_bottom_right, -1f)
            a.recycle()
        }
        if (radius > 0) {
            cornerRadius = radius
        }

        setCornerRadiusInternal(Corner.TOP_LEFT, radiusTopLeft)
        setCornerRadiusInternal(Corner.TOP_RIGHT, radiusTopRight)
        setCornerRadiusInternal(Corner.BOTTOM_LEFT, radiusBottomLeft)
        setCornerRadiusInternal(Corner.BOTTOM_RIGHT, radiusBottomRight)

        if (borderWidth != -1f) {
            super.setBorderWidth(borderWidth)
        }
        if (borderColor != 0) {
            super.setBorderColor(borderColor)
        }

        super.setOval(isCircle)

        mutateBackground(true)

        if (background != null) {
            setBackgroundDrawable(background)
        }
    }

    private fun setCornerRadiusInternal(corner: Int, radius: Float) {
        if (radius > 0) {
            setCornerRadius(corner, radius)
        }
    }
}
