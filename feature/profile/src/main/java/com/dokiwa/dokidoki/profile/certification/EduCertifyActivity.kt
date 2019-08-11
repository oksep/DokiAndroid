package com.dokiwa.dokidoki.profile.certification

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseSelectImageActivity
import com.dokiwa.dokidoki.center.ext.glideUrl
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.model.Edu
import com.dokiwa.dokidoki.center.plugin.model.educationToString
import com.dokiwa.dokidoki.center.uploader.SimpleUploader
import com.dokiwa.dokidoki.center.util.toUploadFileSingle
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.dialog.EduPickerDialog
import com.dokiwa.dokidoki.profile.dialog.YearPickerDialog
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.activity_certify_edu.*

private const val TAG = "EduCertifyActivity"

class EduCertifyActivity : BaseSelectImageActivity() {

    companion object {

        internal const val REQUEST_CODE = 0x0002

        fun launch(context: Activity) {
            context.startActivityForResult(Intent(context, EduCertifyActivity::class.java), REQUEST_CODE)
        }
    }

    data class EducationPayload(
        var name: String = "",
        var number: String = "",
        var education: Int = 0,
        var graduationYear: Int = 0,
        var school: String = "",
        var image: String = ""
    )

    private val payload by lazy { EducationPayload() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certify_edu)

        educationContainer.setOnClickListener {
            EduPickerDialog.create(this, Edu.LOW) { edu ->
                payload.education = edu
                educationEditText.text = edu.educationToString(this)
                checkPayload()
            }.show()
        }

        yearContainer.setOnClickListener {
            YearPickerDialog.create(this, 0) { year ->
                payload.graduationYear = year
                yearEditText.text = year.toString()
                checkPayload()
            }.show()
        }

        val textWatcher = object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPayload()
            }
        }

        nameEditText.addTextChangedListener(textWatcher)
        numberEditText.addTextChangedListener(textWatcher)
        schoolEditText.addTextChangedListener(textWatcher)

        image.setOnClickListener {
            selectImage(R.string.profile_certify_edu_certify_image_choose)
        }

        submitBtn.setOnClickListener {
            submitPayload()
        }
    }

    private fun checkPayload() {
        payload.name = nameEditText.text.toString()
        payload.number = numberEditText.text.toString()
        payload.school = schoolEditText.text.toString()

        val enable = payload.name.isNotEmpty() &&
                payload.education > 0 &&
                payload.graduationYear > 0 &&
                payload.school.isNotEmpty() &&
                payload.image.isNotEmpty()

        submitBtn.isEnabled = enable
    }

    private fun submitPayload() {
        SimpleUploader.uploadImage(Uri.parse(payload.image), SimpleUploader.ImageType.PRIVATE)
            .flatMap {
                Api.get(ProfileApi::class.java)
                    .updateCertifyEducation(
                        payload.name,
                        payload.number,
                        payload.education,
                        payload.graduationYear,
                        payload.school,
                        it.image.rawUrl
                    )
            }
            .subscribeApiWithDialog(this, this, {
                toast(R.string.profile_certify_submitted)
                setResult(Activity.RESULT_OK)
                finish()
            }, {
                toastApiException(it, R.string.center_toast_loading_failed_retry)
            })
    }

    override fun onSelectImageFromCamera(uri: Uri) {
        super.onSelectImageFromCamera(uri)
        resolveImageUri(uri)
    }

    override fun onSelectImageFromGallery(uri: Uri) {
        super.onSelectImageFromGallery(uri)
        resolveImageUri(uri)
    }

    private fun resolveImageUri(uri: Uri) {
        uri.toUploadFileSingle(this).subscribe({
            Log.d(TAG, "resolveImageUri -> $it")
            image.glideUrl(it)
            payload.image = it
        }, {
            checkPayload()
            Log.e(TAG, "resolveImageUri failed", it)
        }).also {
            addDispose(it)
        }
    }
}
