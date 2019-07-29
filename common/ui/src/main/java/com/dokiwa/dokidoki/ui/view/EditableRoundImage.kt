package com.dokiwa.dokidoki.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.widget.ImageView
import androidx.core.graphics.toRectF
import androidx.core.view.GestureDetectorCompat
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.util.DragSortHelper

class EditableRoundImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr), DragSortHelper.IDragSortView {

    private val h: Int
    private val w: Int
    private val icon: Drawable?
    private val detector: GestureDetectorCompat?
    private var isFLy = false
    private var dragAble = true
    private var showEditIcon = true

    var onCloseListener: (() -> Unit)? = null
    var editTag: Any? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditableRoundImage)
        icon = typedArray.getDrawable(R.styleable.EditableRoundImage_editIcon)?.mutate()
        w = typedArray.getDimensionPixelSize(R.styleable.EditableRoundImage_editIconWidth, 0)
        h = typedArray.getDimensionPixelSize(R.styleable.EditableRoundImage_editIconHeight, 0)
        typedArray.recycle()

        detector = if (icon != null) {
            GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return isTouchInIconArea(e) && showEditIcon
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (showEditIcon) {
                        onCloseListener?.invoke()
                        playSoundEffect(SoundEffectConstants.CLICK)
                    }
                    return showEditIcon
                }
            })
        } else {
            null
        }
    }

    private fun isTouchInIconArea(e: MotionEvent): Boolean {
        return icon?.bounds?.toRectF()?.contains(e.x, e.y) ?: false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector?.onTouchEvent(event) == true || super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        icon?.setBounds(width - w, 0, width, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isFLy && showEditIcon) {
            icon?.draw(canvas)
        }
    }

    fun showEditIcon(show: Boolean) {
        this.showEditIcon = show
        invalidate()
    }

    override fun setDragAble(dragAble: Boolean) {
        this.dragAble = dragAble
    }

    override fun isDragAble(): Boolean {
        return this.dragAble
    }

    override fun onFly() {
        isFLy = true
        animate().scaleX(0.85f).scaleY(0.85f).alpha(0.75f).start()
        invalidate()
    }

    override fun onGround() {
        isFLy = false
        animate().scaleX(1f).scaleY(1f).alpha(1f).start()
        invalidate()
    }
}