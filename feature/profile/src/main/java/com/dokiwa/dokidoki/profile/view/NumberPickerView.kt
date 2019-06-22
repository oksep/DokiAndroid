package com.dokiwa.dokidoki.profile.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.dokiwa.dokidoki.profile.R

/**
 * Created by Septenary on 2019/2/12.
 */
open class NumberPickerView : ConstraintLayout {

    private var cb: ((Int) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    val list = IntRange(80, 250).toList()

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        LayoutInflater.from(context).inflate(R.layout.view_picker_number, this)

        val numberPicker = findViewById<WheelView>(R.id.numberPicker)
        numberPicker.adapter = ArrayWheelAdapter(list)

        findViewById<TextView>(R.id.pickerCancelBtn).setOnClickListener {
            cancel()
        }
        findViewById<TextView>(R.id.pickerConfirmBtn).setOnClickListener {
            cb?.invoke(currentValue)
            confirm()
        }
    }

    private val currentValue: Int
        get() = findViewById<WheelView>(R.id.numberPicker).run {
            adapter.getItem(currentItem) as Int
        }

    fun setCurrentNumber(number: Int) {
        findViewById<WheelView>(R.id.numberPicker).run {
            this.currentItem = list.indexOf(number)
        }
    }

    fun setOnNumberSelectListener(cb: (Int) -> Unit) {
        this.cb = cb
        this.cb?.invoke(currentValue)
    }

    fun show() {
        this.animate().translationY(0f).start()
    }

    fun hide() {
        this.animate().translationY(height.toFloat()).start()
    }

    open fun cancel() {
        hide()
    }

    open fun confirm() {
        hide()
    }
}