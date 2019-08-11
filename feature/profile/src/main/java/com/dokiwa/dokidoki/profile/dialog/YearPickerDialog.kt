package com.dokiwa.dokidoki.profile.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.ViewGroup
import com.dokiwa.dokidoki.center.dialog.BottomDialog
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.view.DatePickerView
import java.util.*

/**
 * Created by Septenary on 2019-08-11.
 */
class YearPickerDialog(
    context: Activity,
    private val year: Int,
    private val callback: (Int) -> Unit
) : BottomDialog(context) {

    companion object {
        fun create(context: Activity, year: Int, callback: (Int) -> Unit): Dialog {
            return YearPickerDialog(context, year, callback)
        }
    }

    init {
        val pickerView = object : DatePickerView(context) {
            override fun cancel() {
                this@YearPickerDialog.cancel()
            }

            override fun confirm() {
                this@YearPickerDialog.cancel()
            }
        }
        Calendar.getInstance().set(Calendar.YEAR, year)
        pickerView.initView(booleanArrayOf(true, false, false, false, false, false))
        pickerView.setOnDateSelectListener {
            callback.invoke(it.get(Calendar.YEAR))
        }
        pickerView.setBackgroundColor(Color.WHITE)
        setContentView(
            pickerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.profile_picker_view_height)
            )
        )
    }
}

