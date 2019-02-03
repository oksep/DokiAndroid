package com.dokiwa.dokidoki.profile.create.fragment

import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.fragment_create_profile_nick.*

/**
 * Created by Septenary on 2019/1/2.
 */
internal class NickFragment : BaseStepFragment() {

    override val layoutId: Int = R.layout.fragment_create_profile_nick

    override val skipable: Boolean = false

    override fun updateContent(viewModel: SharedViewModel) {
        confirmBtn.setOnClickListener {
            if (editText.text.isNullOrEmpty()) {
                requireContext().toast(R.string.profile_create_profile_nick_error)
            } else {
                viewModel.nick.value = editText.text.toString()
                requestNextStep()
            }
        }
    }

    override fun reset() {
    }
}