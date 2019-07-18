package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.dokiwa.dokidoki.center.ext.glideFile
import com.dokiwa.dokidoki.center.ext.glideUrl
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.ext.getScreenWidth
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment

/**
 * Created by Septenary on 2019-06-23.
 */
class AttachmentImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    fun setAttachment(attachment: ImageAttachment) {
        val w = attachment.width
        val h = attachment.height
        val maxSize = (getScreenWidth() * 0.5f).toInt()
        val lp = layoutParams
        when {
            (w <= 0 || h <= 0 || w == h) -> {
                lp.width = maxSize
                lp.height = maxSize
            }
            w > h -> {
                val ratio = 1f * h / w
                lp.width = maxSize
                lp.height = (maxSize * ratio).toInt()
            }
            else -> {
                val ratio = 1f * w / h
                lp.width = (maxSize * ratio).toInt()
                lp.height = maxSize
            }
        }

        when {
            !attachment.path.isNullOrEmpty() -> {
                glideFile(attachment.path, 5f, R.drawable.ui_placeholder_radius_5dp)
            }
            !attachment.thumbUrl.isNullOrEmpty() -> {
                glideUrl(attachment.thumbUrl, 5f, R.drawable.ui_placeholder_radius_5dp)
            }
            else -> {
                setImageResource(R.color.dd_gray_middle)
            }
        }
    }
}