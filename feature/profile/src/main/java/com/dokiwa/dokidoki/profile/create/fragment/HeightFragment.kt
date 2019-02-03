package com.dokiwa.dokidoki.profile.create.fragment

import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.fragment_create_profile_height.*

/**
 * Created by Septenary on 2019/1/2.
 */
internal class HeightFragment : BaseStepFragment() {

    override val layoutId: Int = R.layout.fragment_create_profile_height

    override val skipable: Boolean = false

    override fun updateContent(viewModel: SharedViewModel) {
        confirmBtn.setOnClickListener {
            requestNextStep()
        }

        heightText.setOnClickListener {
            numberPickerView.show()
        }

        numberPickerView.setOnNumberSelectListener { value ->
            viewModel.height.value = value
            heightTextView.text = value.toString()
        }
    }

    override fun reset() {
    }
}