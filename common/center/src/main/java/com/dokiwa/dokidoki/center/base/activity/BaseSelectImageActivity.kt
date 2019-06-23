package com.dokiwa.dokidoki.center.base.activity

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.R
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.util.CameraUtil
import com.dokiwa.dokidoki.center.util.Glide4Engine
import com.dokiwa.dokidoki.center.util.now
import com.dokiwa.dokidoki.ui.util.ViewUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.io.IOException

@RuntimePermissions
abstract class BaseSelectImageActivity : TranslucentActivity() {

    companion object {
        private const val TAG = "BaseSelectImageActivity"
        private const val REQUEST_CODE_CAMERA = 0x0005
        private const val REQUEST_CODE_GALLERY = 0x0006
        private const val REQUEST_CODE_MATISSE = 0x0007
    }

    fun selectImage(@StringRes titleRes: Int) {
        AlertDialog.Builder(this)
            .setTitle(titleRes)
            .setItems(
                arrayOf(
                    getString(R.string.center_image_choose_from_camera),
                    getString(R.string.center_image_choose_from_album)
                )
            ) { _, item ->
                if (item == 0) {
                    selectImageByCameraWithPermissionCheck()
                } else if (item == 1) {
                    selectImageByGalleryWithPermissionCheck()
                }
            }.create().also { it.setCanceledOnTouchOutside(true) }.show()
    }

    @NeedsPermission(value = [CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun selectImageByCamera() {
        takePhoto()
    }

    @NeedsPermission(value = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun selectImageByGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.center_image_choose_title)),
            REQUEST_CODE_GALLERY
        )
    }

    fun selectImageByMatisse(max: Int = 9) {
        selectImageByMatisseImplWithPermissionCheck(max)
    }

    @NeedsPermission(value = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun selectImageByMatisseImpl(max: Int = 9) {
        Matisse.from(this)
            .choose(MimeType.ofAll())
            .countable(true)
            .maxSelectable(max)
            // .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .gridExpectedSize(ViewUtil.getScreenWidth() / 3)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(Glide4Engine())
            .theme(R.style.Matisse_Zhihu)
            .forResult(REQUEST_CODE_MATISSE)
    }

    open fun onSelectImageFromCamera(uri: Uri) {
    }

    open fun onSelectImageFromGallery(uri: Uri) {
    }

    open fun onSelectImageFromMatisse(list: List<Uri>) {
    }

    ///////////////////////////////////////////
    // permissions
    ///////////////////////////////////////////
    @OnShowRationale(value = [CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun showRationaleForCamera(request: PermissionRequest) {
        showRationaleDialog(R.string.center_permission_require, request)
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setTitle(R.string.tip)
            .setPositiveButton(R.string.confirm) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.cancel) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }

    @OnNeverAskAgain(value = [CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun onPermissionAskAgain() {
        toast(R.string.center_permission_require)
        Log.w(TAG, "onPermissionAskAgain")
    }

    @OnPermissionDenied(value = [CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun onPermissionDenied() {
        Log.w(TAG, "onPermissionDenied")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    ///////////////////////////////////////////
    // take photo by Camera
    ///////////////////////////////////////////

    private var currentPhotoPath: String? = null

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val tmpDir = File(externalCacheDir ?: cacheDir, "share_file")
        if (!tmpDir.exists()) {
            tmpDir.mkdirs()
        }
        return File.createTempFile("JPEG_${now()}", ".jpg", tmpDir).also {
            currentPhotoPath = it.absolutePath
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }

                // try fronted camera
                if (CameraUtil.hasFrontedCamera()) {
                    takePictureIntent.putExtra("camerasensortype", 2)
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "$packageName.fileProvider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    // fix low API camera crash
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                        takePictureIntent.clipData = ClipData.newRawUri("", photoURI)
                    }

                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
                }
            } ?: run {
                toast(R.string.center_toast_image_choose_no_camera_hint)
            }
        }
    }

    private fun addPictureToGallery() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val file = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("LauncherActivity", "onActivityResult: $requestCode, $resultCode, $data")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    // addPictureToGallery()
                    // onSelectImageFromCamera(data?.extras?.get("data") as? Bitmap)
                    if (currentPhotoPath.isNullOrEmpty()) {
                        Log.w(TAG, "take photo failed cause that currentPhotoPath is empty")
                    } else {
                        onSelectImageFromCamera(Uri.fromFile(File(currentPhotoPath)))
                        currentPhotoPath = null
                    }
                }
                REQUEST_CODE_GALLERY -> {
                    data?.data?.let {
                        onSelectImageFromGallery(it)
                    }
                }
                REQUEST_CODE_MATISSE -> {
                    Matisse.obtainResult(data)?.let {
                        onSelectImageFromMatisse(it)
                    }
                }
            }
        }
    }
}