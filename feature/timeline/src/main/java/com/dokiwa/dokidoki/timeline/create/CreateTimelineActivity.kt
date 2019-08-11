package com.dokiwa.dokidoki.timeline.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseSelectImageActivity
import com.dokiwa.dokidoki.center.ext.glideUrl
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.swap
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.ext.toastApiException
import com.dokiwa.dokidoki.center.plugin.location.ILocationPlugin
import com.dokiwa.dokidoki.center.uploader.SimpleUploader
import com.dokiwa.dokidoki.center.util.toUploadFileSingle
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.timeline.Log
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import com.dokiwa.dokidoki.timeline.api.TimelinePicture
import com.dokiwa.dokidoki.ui.util.DragSortHelper
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import com.dokiwa.dokidoki.ui.view.DragNineGridImageView
import com.dokiwa.dokidoki.ui.view.EditableRoundImage
import com.jaeger.ninegridimageview.NineGridImageViewAdapter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_timeline_create.*
import kotlinx.android.synthetic.main.view_location.*

/**
 * Created by Septenary on 2019-07-02.
 */
class CreateTimelineActivity : BaseSelectImageActivity() {
    companion object {
        private const val TAG = "CreateTimelineActivity"
        fun launch(context: Activity) {
            context.startActivity(Intent(context, CreateTimelineActivity::class.java))
            context.overridePendingTransition(R.anim.ui_anim_activity_bottom_in, R.anim.ui_anim_none)
        }
    }

    private val payload by lazy {
        ViewModelProviders.of(this).get(PayLoadViewModel::class.java)
    }

    private val picturesView by lazy {
        pictures as DragNineGridImageView<SelectImage>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_timeline_create)
        initView()
        initData()
    }

    private fun initView() {
        toolBar.leftTextView.setOnClickListener {
            finish()
        }
        toolBar.rightTextView.setOnClickListener {
            publish()
        }
        payload.content.observe(this, Observer<String> {
            setUpPublishBtn()
        })
        payload.locationMeta.observe(this, Observer<LocationMeta> { location ->
            if (location == null) {
                locationClose.visibility = View.GONE
                locationTitle.setText(R.string.timeline_create_where_r_u)
                locationTitle.isSelected = false
            } else {
                locationClose.visibility = View.VISIBLE
                locationTitle.text = location.name
                locationTitle.isSelected = true
            }
        })
        payload.pictures.observe(this, Observer<List<SelectImage>> { pictures ->
            setUpPublishBtn()
            if (pictures != null) {
                if (pictures.size >= 9) {
                    picturesView.setImagesData(pictures.toMutableList())
                } else {
                    picturesView.setImagesData(
                        pictures.toMutableList().also {
                            it.add(SelectImage("", SelectImage.TYPE_SELECT))
                        }
                    )
                }
            } else {
                picturesView.setImagesData(
                    mutableListOf(SelectImage("", SelectImage.TYPE_SELECT))
                )
            }
        })
        locationTitle.setOnClickListener {
            ILocationPlugin.get().launchPoiActivity(this) { name, latitude, longitude ->
                payload.locationMeta.value = LocationMeta(name, latitude, longitude)
            }
        }
        locationClose.setOnClickListener {
            payload.locationMeta.value = null
        }
        editText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                payload.content.value = s.toString()
            }
        })

        picturesView.setAdapter(picturesAdapter)
        picturesView.setSwapListener(object : DragSortHelper.OnViewSwapListener {
            override fun onSwap(firstView: View, firstPosition: Int, secondView: View, secondPosition: Int) {
                payload.pictures.value?.swap(firstPosition, secondPosition)
            }
        })
    }

    private fun initData() {
        payload.content.value = null
        payload.pictures.value = null
        payload.locationMeta.value = null
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.ui_anim_none, R.anim.ui_anim_activity_bottom_out)
    }

    private fun setUpPublishBtn() {
        val enable = !payload.content.value.isNullOrEmpty() && !payload.pictures.value.isNullOrEmpty()
        toolBar.rightTextView.alpha = if (!enable) 0.5f else 1f
        toolBar.rightTextView.isEnabled = enable
    }

    override fun onSelectImageFromMatisse(list: List<Uri>) {
        val copyFileTasks = list.map { it.toUploadFileSingle(this) }
        Single.zip<String, List<SelectImage>>(copyFileTasks) { paths ->
            paths.map { it as String }.filter { it.isNotEmpty() }.map { SelectImage(it, SelectImage.TYPE_IMAGE) }
        }.subscribe({
            Log.d(TAG, "selectImageFromMatisse -> $it")
            this.payload.pictures.value = (this.payload.pictures.value?.toMutableList() ?: mutableListOf()).run {
                addAll(it)
                if (size > 9) subList(0, 9) else this
            }
        }, {
            Log.e(TAG, "selectImageFromMatisse failed", it)
        }).also {
            addDispose(it)
        }
    }

    private val picturesAdapter by lazy {
        object : NineGridImageViewAdapter<SelectImage>() {
            override fun onDisplayImage(context: Context, view: ImageView, item: SelectImage) {
                val imageView = view as EditableRoundImage
                if (item.type == SelectImage.TYPE_SELECT) {
                    imageView.setImageResource(R.drawable.ui_ic_pictures_add)
                    imageView.setBackgroundResource(R.drawable.ui_bg_pictures_add)
                    imageView.showEditIcon(false)
                    imageView.setDragAble(false)
                } else {
                    imageView.setBackgroundResource(R.color.transparent)
                    imageView.editTag = item
                    imageView.glideUrl(item.path, 6f)
                    imageView.showEditIcon(true)
                    imageView.setDragAble(true)
                }
            }

            override fun generateImageView(context: Context): ImageView {
                return (View.inflate(context, R.layout.view_item_timeline_edit_pictures, null) as EditableRoundImage)
                    .apply {
                        onCloseListener = {
                            val list = payload.pictures.value
                            list?.remove(editTag)
                            payload.pictures.value = list?.toMutableList()
                        }
                    }
            }

            override fun onItemImageClick(c: Context, v: ImageView, i: Int, l: List<SelectImage>) {
                if (l[i].type == SelectImage.TYPE_SELECT) {
                    selectImageByMatisse(9)
                } else {
                    GalleryActivity.launchGallery(this@CreateTimelineActivity, i, l.map { it.path })
                }
            }

            override fun onItemImageLongClick(c: Context, v: ImageView, i: Int, l: List<SelectImage>): Boolean {
                return false
            }
        }
    }

    data class SelectImage(val path: String, val type: Int) {
        companion object {
            const val TYPE_IMAGE = 0
            const val TYPE_SELECT = 1
        }
    }

    private fun publish() {

        val locationMeta = payload.locationMeta.value
        val content = payload.content.value
        val pictures = payload.pictures.value

        val publishTask = { list: List<TimelinePicture> ->
            val urlsString = list.joinToString(",") { it.rawUrl }
            Api.get(TimelineApi::class.java).run {
                createTimeline(
                    pictures = urlsString,
                    content = content,
                    positionLongitude = locationMeta?.longitude ?: 0.0,
                    positionLatitude = locationMeta?.latitude ?: 0.0,
                    positionName = locationMeta?.name ?: ""
                )
            }
        }

        val uploadPictures: Single<List<TimelinePicture>> = uploadPictures(pictures)

        uploadPictures.flatMap {
            publishTask(it)
        }.subscribeApiWithDialog(this, this, {
            Log.d(TAG, "updateProfile success -> $it")
            toast(R.string.timeline_create_publish_success)
            finish()
        }, {
            Log.e(TAG, "updateProfile failed", it)
            toastApiException(it, R.string.timeline_create_publish_failed)
        })
    }

    private fun uploadPictures(pictures: List<SelectImage>?): Single<List<TimelinePicture>> {
        val uploadTasks = pictures?.filter { it.type == SelectImage.TYPE_IMAGE }?.map {
            val path = it.path
            if (path.startsWith("http")) {
                Single.just(SimpleUploader.UploadImageResult(SimpleUploader.UploadImageResult.Image(path, path, path)))
            } else {
                SimpleUploader.uploadImage(Uri.parse(path), SimpleUploader.ImageType.PICTURE)
            }
        }

        return if (uploadTasks != null) {
            Single.zip<SimpleUploader.UploadImageResult, List<TimelinePicture>>(uploadTasks) { results ->
                results.map {
                    it as SimpleUploader.UploadImageResult
                }.map {
                    val url = it.image.rawUrl
                    TimelinePicture(url, url, url)
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        } else {
            Single.just(listOf())
        }
    }
}