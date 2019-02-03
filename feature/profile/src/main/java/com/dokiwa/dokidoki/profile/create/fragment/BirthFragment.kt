package com.dokiwa.dokidoki.profile.create.fragment

import android.app.DatePickerDialog
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.fragment_create_profile_birth.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by Septenary on 2019/1/2.
 */
internal class BirthFragment : BaseStepFragment() {

    override val layoutId: Int = R.layout.fragment_create_profile_birth

    override val skipable: Boolean = false

    override fun updateContent(viewModel: SharedViewModel) {

        dateContainerView.setOnClickListener {
            datePickerView.show()
        }

        datePickerView.setOnDateSelectListener { date ->
            yearTextView.text = date.get(Calendar.YEAR).toString()
            monthTextView.text = (date.get(Calendar.MONTH) + 1).toString()
            dayTextView.text = date.get(Calendar.DAY_OF_MONTH).toString()
        }

        confirmBtn.setOnClickListener {
            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(datePickerView.selectDate.time)
            viewModel.birth.value = date
            requestNextStep()
        }
    }

    private fun showDatePickerDialog() {
        val ca = Calendar.getInstance()
        val year = ca.get(Calendar.YEAR)
        val month = ca.get(Calendar.MONTH)
        val day = ca.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(
            requireContext(),
            R.style.SpinnerDatePickerDialog,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val data = (month + 1).toString() + "月-" + dayOfMonth + "日 "
            },
            year, month, day
        ).show()
    }

    override fun reset() {
    }
}