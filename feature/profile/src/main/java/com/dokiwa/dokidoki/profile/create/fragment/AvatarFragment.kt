package com.dokiwa.dokidoki.profile.create.fragment

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.CreateProfileActivity
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import com.dokiwa.dokidoki.profile.crop.CropImageActivity
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver
import kotlinx.android.synthetic.main.fragment_create_profile_avatar.*

/**
 * Created by Septenary on 2019/1/2.
 */
private const val TAG = "AvatarFragment"

internal class AvatarFragment : BaseStepFragment(), CropIwaResultReceiver.Listener {

    override val layoutId: Int = R.layout.fragment_create_profile_avatar

    override val skipable: Boolean = true

    private val cropResultReceiver by lazy { CropIwaResultReceiver() }

    override fun updateContent(viewModel: SharedViewModel) {
        sharedViewModel.avatar.value = null

        val res = if (viewModel.gender.value == Gender.MALE) {
            R.drawable.ic_avatar_default_male
        } else {
            R.drawable.ic_avatar_default_female
        }
        avatarImage.setImageResource(res)
        avatarImage.setOnClickListener {
            (requireContext() as? CreateProfileActivity)?.chooseImage(R.string.profile_create_profile_avatar_choose)
        }

        confirmBtn.setOnClickListener {
            requestNextStep()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropResultReceiver.setListener(this)
        cropResultReceiver.register(requireContext())
    }

    override fun onDestroy() {
        cropResultReceiver.unregister(requireContext())
        super.onDestroy()
    }

    override fun reset() {
    }

    override fun onChooseImageFromAlbum(uri: Uri) {
        CropImageActivity.launch(requireActivity(), uri)
    }

    override fun onChooseImageFromCamera(uri: Uri) {
        CropImageActivity.launch(requireActivity(), uri)
    }

    override fun onCropSuccess(croppedUri: Uri) {
        Log.d(TAG, "onCropSuccess uri: $croppedUri")
        Glide.with(this).load(croppedUri).into(avatarImage)
        sharedViewModel.avatar.value = croppedUri
    }

    override fun onCropFailed(e: Throwable?) {
        Log.w(TAG, "onCropFailed", e)
        requireContext().toast(R.string.profile_create_profile_avatar_upload_failed)
    }
}