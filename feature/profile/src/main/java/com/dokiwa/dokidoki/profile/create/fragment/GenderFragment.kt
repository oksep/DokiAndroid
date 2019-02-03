package com.dokiwa.dokidoki.profile.create.fragment

import android.view.View
import androidx.lifecycle.Observer
import com.dokiwa.dokidoki.profile.Gender
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.fragment_create_profile_gener.*

/**
 * Created by Septenary on 2019/1/2.
 */
internal class GenderFragment : BaseStepFragment() {

    override val layoutId: Int = R.layout.fragment_create_profile_gener

    override val skipable: Boolean = false

    override fun updateContent(viewModel: SharedViewModel) {
        viewModel.gender.observe(this, Observer {
            when (it) {
                Gender.MALE -> {
                    maleImage.isSelected = true
                    femaleImage.isSelected = false
                    confirmBtn.visibility = View.VISIBLE
                }
                Gender.FEMALE -> {
                    maleImage.isSelected = false
                    femaleImage.isSelected = true
                    confirmBtn.visibility = View.VISIBLE
                }
                else -> {
                    maleImage.isSelected = false
                    femaleImage.isSelected = false
                }
            }
        })
        maleImage.setOnClickListener {
            viewModel.gender.value = Gender.MALE
        }
        femaleImage.setOnClickListener {
            viewModel.gender.value = Gender.FEMALE
        }
        confirmBtn.setOnClickListener {
            requestNextStep()
        }
    }

    override fun reset() {
    }
}