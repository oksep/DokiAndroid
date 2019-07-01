package com.dokiwa.dokidoki.profile.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseSelectImageActivity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.glideUri
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.swap
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfile.*
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.center.uploader.SimpleUploader
import com.dokiwa.dokidoki.center.uploader.SimpleUploader.ImageType
import com.dokiwa.dokidoki.center.uploader.SimpleUploader.UploadImageResult
import com.dokiwa.dokidoki.center.util.toUploadFileSingle
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.profile.crop.CropImageActivity
import com.dokiwa.dokidoki.profile.dialog.CityPickerDialog
import com.dokiwa.dokidoki.profile.dialog.EduPickerDialog
import com.dokiwa.dokidoki.profile.dialog.HeightPickerDialog
import com.dokiwa.dokidoki.profile.dialog.IndustryPickerDialog
import com.dokiwa.dokidoki.ui.util.DragSortHelper
import com.dokiwa.dokidoki.ui.view.DragNineGridImageView
import com.dokiwa.dokidoki.ui.view.EditableRoundImage
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
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

        // fix auto gain focus
        nameEditText.isFocusableInTouchMode = false
        nameEditText.post {
            nameEditText.isFocusableInTouchMode = true
        }

        // crop result
        cropResultReceiver.setListener(this)
        cropResultReceiver.register(this)

        setUpViews(rawProfile)

        toolBar.rightTextView.setOnClickListener {
            saveProfile()
        }
    }

    override fun onDestroy() {
        cropResultReceiver.unregister(this)
        super.onDestroy()
    }

    private fun setUpViews(newProfile: UserProfile) {
        this.newProfile = newProfile.copy()
        avatar.glideAvatar(newProfile)
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
            (pictures as DragNineGridImageView<Picture>).apply {
                visibility = View.VISIBLE
                setSwapListener(object : DragSortHelper.OnViewSwapListener {
                    override fun onSwap(firstView: View, firstPosition: Int, secondView: View, secondPosition: Int) {
                        val sortList = list.toMutableList().swap(firstPosition, secondPosition)
                        newProfile = newProfile.copy(pictures = sortList)
                    }
                })
                setAdapter(picturesAdapter)
                setImagesData(list)
            }
            uploadPicturesBtn.visibility = if (list.size >= 9) View.GONE else View.VISIBLE
            picturesCounts.text = getString(R.string.profile_edit_pictures_counts, list.size)
        }
    }

    private fun showEnsureRemovePictureDialog(tag: Picture?) {
        AlertDialog.Builder(this@ProfileEditActivity, R.style.AppCompatAlertDialogStyle)
            .setTitle(R.string.tip)
            .setMessage(R.string.profile_edit_pic_del_message)
            .setNegativeButton(R.string.cancel) { d, _ -> d.cancel() }
            .setPositiveButton(R.string.confirm) { d, _ ->
                d.cancel()
                val list = newProfile.pictures?.toMutableList()
                list?.remove(tag)
                setUpViews(newProfile.copy(pictures = list))
            }
            .create().show()
    }

    private val picturesAdapter by lazy {
        object : NineGridImageViewAdapter<Picture>() {
            override fun onDisplayImage(context: Context, imageView: ImageView, item: Picture) {
                imageView.glideUri(Uri.parse(item.adaptUrl()), 6f)
                (imageView as? EditableRoundImage)?.editTag = item
            }

            override fun generateImageView(context: Context): ImageView {
                return (View.inflate(
                    context,
                    R.layout.view_item_profile_edit_pictures,
                    null
                ) as EditableRoundImage).apply {
                    onCloseListener = {
                        showEnsureRemovePictureDialog(editTag as? Picture)
                    }
                }
            }

            override fun onItemImageClick(
                context: Context?,
                imageView: ImageView?,
                index: Int,
                list: List<Picture>?
            ) {
                list?.let { l ->
                    GalleryActivity.launchGallery(this@ProfileEditActivity, index, l.map { it.adaptUrl() })
                }
            }

            override fun onItemImageLongClick(
                context: Context?,
                imageView: ImageView?,
                index: Int,
                list: MutableList<Picture>?
            ): Boolean {
                return false
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
            val city = City(code, name, Province(province))
            setUpViews(newProfile.copy(city = city))
        }.show()
    }

    fun onChangeIndustryClick(view: View) {
        IndustryPickerDialog.create(this) { _, _, subId, subName ->
            setUpViews(newProfile.copy(industry = Industry(subId, subName)))
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
                    val tags = data?.getStringArrayExtra(TagsEditActivity.EXTRA_TAGS)?.map { Tag(it) }
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
        val copyFileTasks = list.map { it.toUploadFileSingle(this) }
        Single.zip<String, List<Picture>>(copyFileTasks) { paths ->
            paths.map { it as String }.filter { it.isNotEmpty() }.map { Picture(it, it, it) }
        }.subscribe({
            Log.d(TAG, "selectImageFromMatisse -> $it")
            val newPictures = (newProfile.pictures?.toMutableList()?.apply { addAll(it) } ?: it).run {
                if (size > 9) subList(0, 9) else this
            }
            setUpViews(newProfile.copy(pictures = newPictures))
        }, {
            Log.e(TAG, "selectImageFromMatisse failed", it)
        }).also {
            addDispose(it)
        }
    }

    override fun onCropSuccess(croppedUri: Uri) {
        Log.d(TAG, "onCropSuccess uri: $croppedUri")
        val path = croppedUri.toString()
        setUpViews(newProfile.copy(avatar = Avatar(path, path, path)))
    }

    override fun onCropFailed(e: Throwable?) {
        Log.w(TAG, "onCropFailed", e)
        toast(R.string.profile_edit_crop_failed)
    }

    private fun showConnectUsDialog() {
        AlertDialog.Builder(this).setTitle(R.string.tip).setMessage(R.string.profile_edit_alert_message)
            .setNegativeButton(R.string.confirm) { d, _ ->
                d.cancel()
            }.create().show()
    }

    private fun saveProfile() {
        // TODO: 2019-06-22 @Septenary intro、nickname 字段非法 问题
        fun updateProfile(avatar: Avatar, remotePictures: List<Picture>): Single<UserProfileWrap> {
            newProfile = newProfile.copy(pictures = remotePictures, avatar = avatar)
            return Api.get(ProfileApi::class.java).upateProfile(
                avatar = newProfile.avatar.rawUrl,
                nickname = newProfile.nickname,
                gender = newProfile.gender,
                birthday = newProfile.birthday,
                height = newProfile.height,
                education = newProfile.education,
                cityCode = newProfile.city?.code,
                industryCode = newProfile.industry?.id,
                income = newProfile.income,
                intro = newProfile.intro,
                pictures = newProfile.pictures?.map { it.rawUrl }?.joinToString(",") { it },
                keywords = newProfile.tags?.map { it.name }?.joinToString(",") { it }
            )
        }

        val uploadAvatar: Single<Avatar> = uploadAvatar()
        val uploadPictures: Single<List<Picture>> = uploadPictures()

        uploadAvatar.zipWith(
            uploadPictures,
            BiFunction<Avatar, List<Picture>, Pair<Avatar, List<Picture>>> { t1, t2 ->
                Pair(t1, t2)
            }
        ).flatMap {
            updateProfile(it.first, it.second)
        }.subscribeApiWithDialog(this, this, {
            Log.d(TAG, "updateProfile success -> $it")
            toast(R.string.profile_edit_save_success)
            finish()
        }, {
            Log.e(TAG, "updateProfile failed", it)
            toast(R.string.profile_edit_save_failed)
        })
    }

    private fun uploadPictures(): Single<List<Picture>> {
        val uploadTasks = newProfile.pictures?.map {
            val path = it.rawUrl
            if (path.startsWith("http")) {
                Single.just(UploadImageResult(SimpleUploader.UploadImageResult.Image(path, path, path)))
            } else {
                SimpleUploader.uploadImage(Uri.parse(path), SimpleUploader.ImageType.PICTURE)
            }
        }

        return if (uploadTasks != null) {
            Single.zip<UploadImageResult, List<Picture>>(uploadTasks) { results ->
                results.map {
                    it as UploadImageResult
                }.map {
                    val url = it.image.rawUrl
                    Picture(url, url, url)
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        } else {
            Single.just(listOf())
        }
    }

    private fun uploadAvatar(): Single<Avatar> {
        val path = newProfile.avatar.rawUrl
        return if (path.startsWith("http") || path.isEmpty()) {
            Single.just(newProfile.avatar)
        } else {
            SimpleUploader.uploadImage(Uri.parse(path), ImageType.AVATAR).map {
                val url = it.image.rawUrl
                Avatar(url, url, url)
            }
        }
    }
}
