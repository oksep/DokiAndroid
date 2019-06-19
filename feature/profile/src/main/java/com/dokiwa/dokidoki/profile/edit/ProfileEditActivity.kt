package com.dokiwa.dokidoki.profile.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.base.activity.BaseSelectImageActivity
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.ext.loadUri
import com.dokiwa.dokidoki.center.ext.swap
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.crop.CropImageActivity
import com.dokiwa.dokidoki.profile.dialog.CityPickerDialog
import com.dokiwa.dokidoki.profile.dialog.EduPickerDialog
import com.dokiwa.dokidoki.profile.dialog.HeightPickerDialog
import com.dokiwa.dokidoki.profile.dialog.IndustryPickerDialog
import com.dokiwa.dokidoki.ui.util.DragSortHelper
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import com.dokiwa.dokidoki.ui.view.DragNineGridImageView
import com.jaeger.ninegridimageview.NineGridImageView
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.view_profile_edit_pictures.*
import kotlinx.android.synthetic.main.view_profile_edit_pictures_empty.*

private const val TAG = "ProfileEditActivity"

class ProfileEditActivity : BaseSelectImageActivity(), CropIwaResultReceiver.Listener {

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
        setUpPictures()
    }

    private fun setUpPictures() {
        val list = newProfile.pictures
        if (list.isNullOrEmpty()) {
            pictures.visibility = View.GONE
            picturesEmpty.visibility = View.VISIBLE
            uploadPicturesBtn.visibility = View.VISIBLE
            picturesCounts.text = getString(R.string.profile_edit_pictures_counts, 0)
        } else {
            picturesEmpty.visibility = View.GONE
            (pictures as DragNineGridImageView<UserProfile.Picture>).apply {
                visibility = View.VISIBLE
                setSwapListner(object : DragSortHelper.OnViewSwapListener {
                    override fun onSwap(firstView: View, firstPosition: Int, secondView: View, secondPosition: Int) {
                        val sortList = list.toMutableList().swap(firstPosition, secondPosition)
                        newProfile = newProfile.copy(pictures = sortList)
                    }
                })
                setAdapter(picturesAdapter)
                setImagesData(list, NineGridImageView.NOSPAN)
            }
            uploadPicturesBtn.visibility = if (list.size >= 9) View.GONE else View.VISIBLE
            picturesCounts.text = getString(R.string.profile_edit_pictures_counts, list.size)
        }
    }

    private val picturesAdapter by lazy {
        object : NineGridImageViewAdapter<UserProfile.Picture>() {
            override fun onDisplayImage(context: Context, imageView: ImageView, item: UserProfile.Picture) {
                imageView.loadUri(Uri.parse(item.adaptUrl()))
            }

            override fun generateImageView(context: Context): ImageView {
                return View.inflate(context, R.layout.view_item_profile_detail_pictures, null) as ImageView
            }

            override fun onItemImageClick(
                context: Context?,
                imageView: ImageView?,
                index: Int,
                list: List<UserProfile.Picture>?
            ) {
                list?.let { l ->
                    GalleryActivity.launchGallery(this@ProfileEditActivity, index, l.map { it.adaptUrl() })
                }
            }
        }
    }

    fun onChangeAvatarClick(view: View) {
        selectImage(R.string.profile_create_profile_avatar_choose)
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

    fun onChangePicturesClick(view: View) {
        val count = 9 - (newProfile.pictures?.size ?: 0)
        selectImageByMatisse(count)
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

    override fun onSelectImageFromCamera(uri: Uri) {
        CropImageActivity.launch(this, uri)
    }

    override fun onSelectImageFromGallery(uri: Uri) {
        CropImageActivity.launch(this, uri)
    }

    override fun onSelectImageFromMatisse(list: List<Uri>) {
        val pictures = list.map {
            val path = it.toString()
            UserProfile.Picture(path, path, path)
        }
        val newPictures = if (newProfile.pictures.isNullOrEmpty()) {
            pictures
        } else {
            newProfile.pictures!!.toMutableList() + pictures
        }.run {
            if (size > 9) subList(0, 9) else this
        }
        setUpViews(newProfile.copy(pictures = newPictures))
    }

    override fun onCropSuccess(croppedUri: Uri) {
        Log.d(TAG, "onCropSuccess uri: $croppedUri")
        val path = croppedUri.toString()
        setUpViews(newProfile.copy(avatar = UserProfile.Avatar(path, path, path)))
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
