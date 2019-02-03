package com.dokiwa.dokidoki.profile.create.fragment

import com.dokiwa.dokidoki.profile.Gender
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import kotlinx.android.synthetic.main.fragment_create_profile_avatar.*

/**
 * Created by Septenary on 2019/1/2.
 */
internal class AvatarFragment : BaseStepFragment() {

    override val layoutId: Int = R.layout.fragment_create_profile_avatar

    override val skipable: Boolean = true

    override fun updateContent(viewModel: SharedViewModel) {
        val res = if (viewModel.gender.value == Gender.MALE) {
            R.drawable.ic_avatar_default_male
        } else {
            R.drawable.ic_avatar_default_female
        }
        avatarImage.setImageResource(res)
    }

    override fun reset() {
    }
}