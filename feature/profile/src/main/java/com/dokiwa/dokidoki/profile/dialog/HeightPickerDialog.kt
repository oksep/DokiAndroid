package com.dokiwa.dokidoki.profile.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.ViewGroup
import com.dokiwa.dokidoki.center.dialog.BottomDialog
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.view.NumberPickerView

/**
 * Created by Septenary on 2019-06-08.
 */
class HeightPickerDialog(
    context: Activity,
    private val height: Int,
    private val callback: (Int) -> Unit
) : BottomDialog(context) {

    companion object {
        fun create(context: Activity, height: Int, callback: (Int) -> Unit): Dialog {
            return HeightPickerDialog(context, height, callback)
        }
    }

    init {
        val pickerView = object : NumberPickerView(context) {
            override fun cancel() {
                this@HeightPickerDialog.cancel()
            }

            override fun confirm() {
                this@HeightPickerDialog.cancel()
            }
        }
        pickerView.setCurrentNumber(height)
        pickerView.setOnNumberSelectListener {
            callback.invoke(it)
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

