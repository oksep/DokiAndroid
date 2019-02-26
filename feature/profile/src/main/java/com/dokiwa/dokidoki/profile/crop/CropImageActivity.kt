package com.dokiwa.dokidoki.profile.crop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.profile.R
import com.steelkiwi.cropiwa.CropIwaView
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig
import java.io.File

class CropImageActivity : BaseActivity() {

    private val cropView by lazy { findViewById<CropIwaView>(R.id.crop_view) }

    companion object {
        const val KEY_IMAGE_URI = "key_img_uri"
        const val KEY_CROP_WIDTH = "key_crop_width"
        const val KEY_CROP_HEIGHT = "key_crop_height"

        fun launch(activity: Activity, uri: Uri, cropWidth: Int = 480, cropHeight: Int = 480) {
            val intent = Intent(activity, CropImageActivity::class.java)
            intent.putExtra(KEY_IMAGE_URI, uri)
            intent.putExtra(KEY_CROP_WIDTH, cropWidth)
            intent.putExtra(KEY_CROP_HEIGHT, cropHeight)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)
        val uri = intent.getParcelableExtra<Uri>(KEY_IMAGE_URI)
        this.cropView.setImageUri(uri)
        cropView.configureImage().setMinScale(.1f).setImageScaleEnabled(true).apply()
    }

    fun onCancelClick(view: View) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private var cropEnable = true // 剪裁标识，防止多点触摸误操作

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        cropEnable = when (ev?.action) {
            MotionEvent.ACTION_DOWN -> false
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> true
            else -> false
        }
        return super.dispatchTouchEvent(ev)
    }

    fun onCompleteClick(view: View) {
        if (!cropEnable) {
            return
        }
        view.isEnabled = false
        cropView.crop(
            CropIwaSaveConfig.Builder(Uri.fromFile(File(filesDir, System.currentTimeMillis().toString())))
                .setSize(
                    intent.getIntExtra(KEY_CROP_WIDTH, 480),
                    intent.getIntExtra(KEY_CROP_HEIGHT, 480)
                )
                .apply {
                    setCompressFormat(Bitmap.CompressFormat.JPEG)
                    setQuality(100)
                }
                .build()
        )
        finish()
    }
}
