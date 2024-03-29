package com.dokiwa.dokidoki.profile.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.dokiwa.dokidoki.profile.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Septenary on 2019/2/12.
 */
open class DatePickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private fun getTime(date: Date): String {
        Log.d("getTime()", "choice date millis: " + date.time)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    fun initView(type: BooleanArray = booleanArrayOf(true, true, true, false, false, false)) {
        val currentDate = Calendar.getInstance()

        selectDate.time = currentDate.time

        val startDate = Calendar.getInstance()
        startDate.set(1900, 1, 1)

        var timePickerView: TimePickerView? = null

        //时间选择器 ，自定义布局
        TimePickerBuilder(context, OnTimeSelectListener { date, v ->
            selectDate.time = date
            this.cb?.invoke(selectDate)
        })
            .setDate(currentDate)
            .setRangDate(startDate, currentDate)
            .setLayoutRes(R.layout.view_picker_date) { v ->
                v.findViewById<View>(R.id.pickerCancelBtn).setOnClickListener {
                    cancel()
                }
                v.findViewById<View>(R.id.pickerConfirmBtn).setOnClickListener {
                    timePickerView?.returnData()
                    confirm()
                }
            }
            .setOutSideCancelable(false)
            .setType(type)
            .isCenterLabel(false) // 是否只显示中间选中项的label文字，false 则每项item全部都带有label。
            .isDialog(false)
            .setDecorView(this)
            .isCenterLabel(false)
            .build()
            .also { timePickerView = it }

        timePickerView?.setKeyBackCancelable(false)
        timePickerView?.show(false)
    }

    private var cb: ((Calendar) -> Unit)? = null

    var selectDate = Calendar.getInstance()

    fun setOnDateSelectListener(cb: (Calendar) -> Unit) {
        this.cb = cb
        this.cb?.invoke(selectDate)
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
