package com.dokiwa.dokidoki.profile.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.dokiwa.dokidoki.center.base.activity.BaseChooseImageActivity
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.crop.CropImageActivity
import com.dokiwa.dokidoki.profile.dialog.CityPickerDialog
import com.dokiwa.dokidoki.profile.dialog.EduPickerDialog
import com.dokiwa.dokidoki.profile.dialog.HeightPickerDialog
import com.dokiwa.dokidoki.profile.dialog.IndustryPickerDialog
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver
import kotlinx.android.synthetic.main.activity_profile_edit.*

private const val TAG = "ProfileEditActivity"

class ProfileEditActivity : BaseChooseImageActivity(), CropIwaResultReceiver.Listener {

    companion object {
        private const val EXTRA_PROFILE = "extra.user.newProfile"

        fun launch(context: Context, profile: UserProfile) {
            context.startActivity(
                Intent(context, ProfileEditActivity::class.java).putExtra(EXTRA_PROFILE, profile)
            )
        }
    }

    private lateinit var newProfile: UserProfile

    private lateinit var rawProfile: UserProfile

    private val cropResultReceiver by lazy { CropIwaResultReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        rawProfile = intent.getParcelableExtra(EXTRA_PROFILE) ?: return finish()

        nameEditText.addTextChangedListener(object : SimpleTextWatcher {

        })

        incomeEditText.addTextChangedListener(object : SimpleTextWatcher {

        })

        // fix auto gain focus
        nameEditText.isFocusableInTouchMode = false
        nameEditText.post {
            nameEditText.isFocusableInTouchMode = true
        }

        // crop result
        cropResultReceiver.setListener(this)
        cropResultReceiver.register(this)

        setUpViews(rawProfile)
    }

    override fun onDestroy() {
        cropResultReceiver.unregister(this)
        super.onDestroy()
    }

    private fun setUpViews(newProfile: UserProfile) {
        this.newProfile = newProfile.copy()
        avatar.loadAvatar(newProfile)
        nameEditText.setText(newProfile.nickname)
        incomeEditText.setText(if (newProfile.income > 0) newProfile.income.toString() else null)
        gender.setText(
            when (newProfile.gender) {
                Gender.FEMALE -> getString(R.string.profile_edit_gender_female)
                Gender.MALE -> getString(R.string.profile_edit_gender_male)
                else -> getString(R.string.profile_edit_gender_unknown)
            }
        )
        birthday.setText(newProfile.getReadableBirthday())
        edu.setText(newProfile.getEduText(this))
        city.setText(newProfile.city?.name)
        industry.setText(newProfile.industry?.name)
        intro.setText(newProfile.intro)
        height.setText(getString(R.string.profile_edit_height_value, newProfile.height))
        if (newProfile.tags.isNullOrEmpty()) {
            tagsEmpty.setText(R.string.profile_edit_tags_hint)
            tagsView.visibility = View.GONE
        } else {
            tagsEmpty.text = null
            tagsView.visibility = View.VISIBLE
            tagsView.setTags(newProfile.tags!!.map { it.name })
        }
    }

    fun onChangeAvatarClick(view: View) {
        chooseImage(R.string.profile_create_profile_avatar_choose)
    }

    fun onChangeGenderClick(view: View) {
        showConnectUsDialog()
    }

    fun onChangeBirthdayClick(view: View) {
        showConnectUsDialog()
    }

    fun onChangeHeightClick(view: View) {
        HeightPickerDialog.create(this, newProfile.height) {
            setUpViews(newProfile.copy(height = it))
        }.show()
    }

    fun onChangeEduClick(view: View) {
        EduPickerDialog.create(this, newProfile.education) { edu ->
            setUpViews(newProfile.copy(education = edu))
        }.show()
    }

    fun onChangeCityClick(view: View) {
        CityPickerDialog.create(this) { province, name, code ->
            val city = UserProfile.City(code, name, UserProfile.Province(province))
            setUpViews(newProfile.copy(city = city))
        }.show()
    }

    fun onChangeIndustryClick(view: View) {
        IndustryPickerDialog.create(this) { id, name, subId, subName ->
            setUpViews(newProfile.copy(industry = UserProfile.Industry(subId, subName)))
        }.show()
    }

    fun onChangeIntroClick(view: View) {
        IntroEditActivity.launch(this, newProfile.intro)
    }

    fun onChangeTagsClick(view: View) {
        TagsEditActivity.launch(this, newProfile.tags?.map { it.name })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IntroEditActivity.REQUEST_CODE -> {
                    setUpViews(newProfile.copy(intro = data?.getStringExtra(IntroEditActivity.EXTRA_INTRO)))
                }
                TagsEditActivity.REQUEST_CODE -> {
                    val tags = data?.getStringArrayExtra(TagsEditActivity.EXTRA_TAGS)?.map { UserProfile.Tag(it) }
                    setUpViews(newProfile.copy(tags = tags))
                }
            }
        }
    }

    override fun onChooseImageFromAlbum(uri: Uri) {
        CropImageActivity.launch(this, uri)
    }

    override fun onChooseImageFromCamera(uri: Uri) {
        CropImageActivity.launch(this, uri)
    }

    override fun onCropSuccess(croppedUri: Uri) {
        Log.d(TAG, "onCropSuccess uri: $croppedUri")
        Glide.with(this).load(croppedUri).into(avatar)
    }

    override fun onCropFailed(e: Throwable?) {
        Log.w(TAG, "onCropFailed", e)
        toast(R.string.profile_create_profile_avatar_upload_failed)
    }

    private fun showConnectUsDialog() {
        AlertDialog.Builder(this).setTitle(R.string.tip).setMessage(R.string.profile_edit_alert_message)
            .setNegativeButton(R.string.confirm) { d, _ ->
                d.cancel()
            }.create().show()
    }

}
