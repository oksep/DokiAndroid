package com.dokiwa.dokidoki.center.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.R
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.ui.ext.dp2px
import java.io.File

/**
 * Created by Septenary on 2019/1/2.
 */
private const val TAG = "ImageViewExt"

fun ImageView.glideUri(uri: Uri, radius: Float = 0f, @DrawableRes default: Int? = null) {

    val option = RequestOptions()
        .placeholder(default ?: R.color.dd_gray_light)
        .error(default ?: R.color.dd_red)
        .apply {
            when {
                (radius < 0) -> circleCrop()
                (radius == 0f) -> centerCrop()
                else -> transforms(CenterCrop(), RoundedCorners(context.dp2px(radius)))
            }
        }

    Glide.with(this)
        .load(uri)
        .listener(LogListener(uri.toString()))
        .apply(option)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.glideUrl(url: String, radius: Float = 0f, @DrawableRes default: Int? = null) {
    glideUri(Uri.parse(url), radius, default)
}

fun ImageView.glideAsset(path: String, radius: Float = 0f, @DrawableRes default: Int? = null) {
    glideUrl("file:///android_asset/$path", radius, default)
}

fun ImageView.glideFile(path: String, radius: Float = 0f, @DrawableRes default: Int? = null) {
    glideUri(Uri.fromFile(File(path)), radius, default)
}

fun ImageView.glideAvatar(profile: UserProfile) {
    val url = profile.avatar.adaptUrl()
    glideUri(
        Uri.parse(url), -1f, if (profile.gender == Gender.FEMALE) {
            R.drawable.ui_ic_avatar_default_female
        } else {
            R.drawable.ui_ic_avatar_default_male
        }
    )
}

fun ImageView.glideAvatar(url: String, gender: Int) {
    glideUri(
        Uri.parse(url), -1f, if (gender == Gender.FEMALE) {
            R.drawable.ui_ic_avatar_default_female
        } else {
            R.drawable.ui_ic_avatar_default_male
        }
    )
}

fun ImageView.glideAvatar(avatar: UserProfile.Avatar) {
    val url = avatar.adaptUrl()
    glideUri(Uri.parse(url), -1f, R.drawable.ui_ic_avatar_default_female)
}

private class LogListener(private val path: String) : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        Log.e(TAG, "failed path:$path", e)
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        Log.d(TAG, "success path:$path")
        return false
    }
}