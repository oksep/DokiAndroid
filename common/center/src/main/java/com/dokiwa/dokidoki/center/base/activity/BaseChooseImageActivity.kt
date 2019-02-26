package com.dokiwa.dokidoki.center.base.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.R
import com.dokiwa.dokidoki.center.constant.Constants
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.util.PermissionUtil
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

abstract class BaseChooseImageActivity : TranslucentActivity() {

    companion object {
        const val REQUEST_PERMISSION_CAMERA = 100
        const val TAG = "BaseChooseImageActivity"
    }

    private val cameraCrop by lazy { ChooseImageHelper(this) }

    fun chooseImage(@StringRes id: Int) {
        cameraCrop.chose(getString(id))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            val index = permissions.indexOf(PermissionUtil.cameraPermission)
            if (index != -1 && grantResults[index] == 0) {
                cameraCrop.startActionCamera()
            }
        }
    }

    override fun shouldShowRequestPermissionRationale(permission: String?) = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $requestCode, $resultCode, $data")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ImageChooseHelper.REQUEST_CAMERA -> {
                    cameraCrop.getUri(data) { uri ->
                        uri?.let {
                            onChooseImageFromCamera(it)
                        }
                    }
                }
                ImageChooseHelper.REQUEST_CALENDAR -> {
                    data?.data?.let {
                        onChooseImageFromAlbum(it)
                    }
                }
            }
        }
    }

    abstract fun onChooseImageFromCamera(uri: Uri)

    abstract fun onChooseImageFromAlbum(uri: Uri)

    inner class ChooseImageHelper(
        val context: Activity
    ) : ImageChooseHelper(context, null, Constants.SHARE_FILE_NAME, "${context.packageName}.fileProvider") {

        override fun startActionCamera() {
            val cameraPermission = PermissionUtil.cameraPermission
            if (!EasyPermissions.hasPermissions(context, cameraPermission)) {
                EasyPermissions.requestPermissions(
                    context,
                    "",
                    REQUEST_PERMISSION_CAMERA,
                    cameraPermission
                )
            } else {
                super.startActionCamera()
            }
        }
    }
}

/**
 * 需要在 AndroidManifest.xml 中进行 [java.security.Provider] 配置
 * <pre>
 * <provider android:name="android.support.v4.content.FileProvider" android:authorities="${applicationId}" android:exported="false" android:grantUriPermissions="true">
 * <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/share_images"></meta-data>
</provider> *
</pre> *
 */
open class ImageChooseHelper(
    private val mContext: Activity,
    private val mFragment: Fragment?,
    private val TEMP_PATH_NAME: String,
    private val SHARE_AUTHORITY: String
) {
    private var portraitPath = ""

    // 拍照保存的绝对路径
    private val cameraTempFile: Uri?
        get() {
            val imagePath = externalTempDir
            val savePath = imagePath.absolutePath
            val tmpFie = File(savePath, System.currentTimeMillis().toString() + ".jpg")
            portraitPath = tmpFie.absolutePath
            var uri: Uri? = null
            try {
                uri = FileProvider.getUriForFile(mContext, SHARE_AUTHORITY, tmpFie)
            } catch (e: Exception) {
                Log.e(TAG, "")
            }
            return uri
        }

    private val externalTempDir: File
        get() {
            val dir = mContext.externalCacheDir ?: mContext.cacheDir
            val file = File(dir.toString() + File.separator + TEMP_PATH_NAME)
            if (!file.exists()) file.mkdirs()
            return file
        }

    private fun startActivityForResult(intent: Intent, resultCode: Int) {
        if (mFragment != null) {
            mFragment.startActivityForResult(intent, resultCode)
        } else {
            mContext.startActivityForResult(intent, resultCode)
        }
    }

    // 打开相机拍照
    open fun startActionCamera() {
        // 检测是否 声明了相机权限 但是 却没有申请相机权限
        if (PermissionUtil.shouldRequestForCameraPermission(mContext)) {
            //  todo callback
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (hasFaceCamera()!!) {
            // 前置摄像头
            intent.putExtra("camerasensortype", 2)
        }
        // 设置照片的质量
        val uri = cameraTempFile ?: return
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            //修复低版本下相机拍摄崩溃问题
            // https://medium.com/@quiro91/sharing-files-through-intents-part-2-fixing-the-permissions-before-lollipop-ceb9bb0eec3a
            intent.clipData = ClipData.newRawUri("", uri)
        }

        // 判断是否支持摄像头拍照
        if (intent.resolveActivity(mContext.packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA)
        } else {
            mContext.toast(R.string.image_choose_no_camera_hint)
        }
    }

    // 打开系统文件选择图片
    fun startImagePick() {
        portraitPath = ""
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, mContext.getString(R.string.image_choose_title)),
            REQUEST_CALENDAR
        )
    }

    // 弹出 选择获取照片方式的对话框
    fun chose(title: String) {

        portraitPath = ""
        val items = arrayOf<CharSequence>(
            mContext.getString(R.string.image_choose_from_camera),
            mContext.getString(R.string.image_choose_from_album)
        )
        val builder = AlertDialog.Builder(mContext)
        if (!TextUtils.isEmpty(title)) builder.setTitle(title)
        builder.setItems(items) { dialog, item ->
            if (item == 0) { // 拍照
                startActionCamera()
            } else if (item == 1) { // 相册
                startImagePick()
            }
        }

        val imageDialog = builder.create()
        imageDialog.setCanceledOnTouchOutside(true)
        imageDialog.show()
    }

    /**
     * 获取拍照后的 Uri，该方法是异步的，结果通过 listener 在主线程中返回
     *
     * @param data     [Activity.onActivityResult]返回的 Intent
     * @param listener 回调接口
     */
    fun getUri(data: Intent?, listener: ((uri: Uri?) -> Unit)?) {
        Thread(Runnable {
            var uri: Uri? = null
            if (!TextUtils.isEmpty(portraitPath)) {
                uri = Uri.fromFile(File(portraitPath))
            } else {
                if (data != null) {
                    uri = data.data
                    Log.d(TAG, "getUri origin url -> " + uri!!)
                    uri = updateUriByVersion(uri)
                }
            }
            returnUriInMain(uri, listener)
        }).start()
    }

    private fun returnUriInMain(uri: Uri?, listener: ((uri: Uri?) -> Unit)?) {
        Handler(Looper.getMainLooper()).post {
            listener?.invoke(uri)
            Log.d(TAG, "getUri return url -> " + uri!!)
        }
    }

    private fun updateUriByVersion(uri: Uri?): Uri? {
        var uri = uri
        if (DocumentsContract.isDocumentUri(mContext, uri)) {
            val file = saveDocumentFile(uri)
            if (file != null) {
                uri = Uri.fromFile(file)
            } else {
                uri = null
            }
        }
        return uri
    }

    private fun saveDocumentFile(uri: Uri?): File? {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        var descriptor: ParcelFileDescriptor? = null
        try {
            descriptor = mContext.contentResolver.openFileDescriptor(uri!!, "r")
            if (descriptor == null) {
                Log.e(TAG, "saveDocumentFile mContext.getContentResolver() return null", null)
                return null
            }
            val fileDescriptor = descriptor.fileDescriptor
            if (!fileDescriptor.valid()) {
                Log.e(TAG, "saveDocumentFile fileDescriptor is not valid", null)
                return null
            }

            fileInputStream = FileInputStream(fileDescriptor)

            val outTmpFile = externalTempDir
            val tmpFile = File(outTmpFile, System.currentTimeMillis().toString() + ".jpg")
            fileOutputStream = FileOutputStream(tmpFile)

            val chunk = ByteArray(1024)
            var re: Int
            while (true) {
                re = fileInputStream.read(chunk)
                if (re > 0) {
                    fileOutputStream.write(chunk, 0, re)
                } else {
                    break
                }
            }

            return tmpFile
        } catch (e: Exception) {
            Log.e(TAG, "readShareFileFromServer error : " + e.message, e.cause)
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
                descriptor?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return null
    }

    fun hasFaceCamera(): Boolean? {
        val cameraInfo = Camera.CameraInfo()
        val cameraCount = Camera.getNumberOfCameras()
        for (i in 0 until cameraCount) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true
            }
        }
        return false
    }

    companion object {
        val REQUEST_CAMERA = 11001
        val REQUEST_CALENDAR = 11002

        private const val TAG = "ImageChooser"
    }
}