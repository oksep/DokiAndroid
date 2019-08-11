package com.dokiwa.dokidoki.profile.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.interfaces.IPickerViewData
import com.contrarywind.view.WheelView
import com.dokiwa.dokidoki.center.plugin.model.Edu
import com.dokiwa.dokidoki.profile.R

/**
 * Created by Septenary on 2019/2/12.
 */
open class EduPickerView : ConstraintLayout {

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

    private val list = Edu.cases.map {
        EduEntity(it.value, it.textRes)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        LayoutInflater.from(context).inflate(R.layout.view_picker_edu, this)

        val eduPicker = findViewById<WheelView>(R.id.eduPicker)
        eduPicker.adapter = ArrayWheelAdapter(list)

        findViewById<TextView>(R.id.pickerCancelBtn).setOnClickListener {
            cancel()
        }
        findViewById<TextView>(R.id.pickerConfirmBtn).setOnClickListener {
            this.cb?.invoke(currentValue.value)
            confirm()
        }
    }

    private val currentValue: EduEntity
        get() = findViewById<WheelView>(R.id.eduPicker).run {
            adapter.getItem(currentItem) as EduEntity
        }

    fun setCurrent(edu: Int) {
        findViewById<WheelView>(R.id.eduPicker).run {
            val index = list.indexOfFirst { it.value == edu }
            this.currentItem = index
        }
    }

    fun setOnNumberSelectListener(cb: (Int) -> Unit) {
        this.cb = cb
        this.cb?.invoke(currentValue.value)
    }

    private fun show() {
        this.animate().translationY(0f).start()
    }

    private fun hide() {
        this.animate().translationY(height.toFloat()).start()
    }

    open fun cancel() {
        hide()
    }

    open fun confirm() {
        hide()
    }

    private inner class EduEntity(
        value: Int,
        textRes: Int
    ) : Edu(value, textRes), IPickerViewData {
        override fun getPickerViewText(): String {
            return resources.getString(textRes)
        }
    }
}