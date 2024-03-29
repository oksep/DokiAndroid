package com.dokiwa.dokidoki.profile.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.ViewGroup
import com.dokiwa.dokidoki.center.dialog.BottomDialog
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.view.EduPickerView

/**
 * Created by Septenary on 2019-06-08.
 */
class EduPickerDialog(
    context: Activity,
    private val edu: Int,
    private val callback: (Int) -> Unit
) : BottomDialog(context) {


    companion object {
        fun create(context: Activity, edu: Int, callback: (Int) -> Unit): Dialog {
            return EduPickerDialog(context, edu, callback)
        }
    }

    init {
        val pickerView = object : EduPickerView(context) {
            override fun cancel() {
                this@EduPickerDialog.cancel()
            }

            override fun confirm() {
                this@EduPickerDialog.cancel()
            }
        }
        pickerView.setCurrent(edu)
        pickerView.setOnNumberSelectListener { edu ->
            callback.invoke(edu)
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

