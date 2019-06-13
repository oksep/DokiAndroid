package com.dokiwa.dokidoki.center.ext

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
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
import com.dokiwa.dokidoki.ui.util.BitmapUtil
import java.io.File

/**
 * Created by Septenary on 2019/1/2.
 */
private const val TAG = "ImageViewExt"

private fun isContextAvaliable(context: Context): Boolean {
    return if (context is Activity) {
        !context.isDestroyed && !context.isFinishing
    } else {
        true
    }
}

fun ImageView.loadImgFromLocal(path: String) {
    loadImgFromLocal(path, 0f)
}

fun ImageView.loadImgFromLocal(path: String, roundingRadius: Float = 0f, isGif: Boolean = false) {
    if (isGif) {
        loadGif(path, roundingRadius)
    } else {
        loadImgFromLocal(path, roundingRadius)
    }
}

fun loadGif(path: String, roundingRadius: Float) {

}

fun ImageView.loadImgFromLocal(path: String, roundingRadius: Float = 0f) {
    if (!isContextAvaliable(context)) {
        Log.d(TAG, "context is invalid")
        return
    }
    RequestOptions()
        .centerCrop()
        .apply {
            if (roundingRadius > 0) {
                transform(RoundedCorners(context.dp2px(roundingRadius)))
            }
        }
        .also {
            Glide.with(this).asDrawable()
                .apply(it)
                .listener(LogGlideListener(path))
                .load(File(path))
                .into(this)
        }
}

fun ImageView.loadCircleImgWithTransition(path: String) {
    if (!isContextAvaliable(context)) {
        Log.d(TAG, "context is invalid")
        return
    }
    RequestOptions()
        .centerCrop()
        .apply {
            transform(CircleCrop())
        }
        .also {
            Glide.with(this).asDrawable()
                .apply(it)
                .listener(LogGlideListener(path))
                .load(File(path))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)
        }
}

private fun hypotenuse(width: Int, height: Int): Float {
    val halfWidth = width / 2.0
    val halfHeight = height / 2.0
    return Math.sqrt(halfWidth * halfWidth + halfHeight * halfHeight).toFloat()
}

private fun ImageView.loadFileBitmap(path: String, scaleWidth: Int = 0, scaleHeight: Int = 0) {
    if (scaleWidth != 0 && scaleHeight != 0) {
        BitmapUtil.getBitmapNative(context, path, scaleWidth, scaleHeight)?.also {
            setImageBitmap(it)
        }
    } else {
        post {
            BitmapUtil.getBitmapNative(context, path, width, height)?.also {
                setImageBitmap(it)
            }
        }
    }
}

fun ImageView.loadImgFromNetWork(url: String) {
    loadImgFromNetWork(url, null, null, null, null)
}

fun ImageView.loadImgFromNetWork(url: String, @DrawableRes placeHolder: Int) {
    loadImgFromNetWork(
        url,
        ContextCompat.getDrawable(context, placeHolder),
        null, null, null
    )
}

fun ImageView.loadAvatar(profile: UserProfile) {
    loadImgFromNetWork(
        profile.avatar.adaptUrl(),
        ContextCompat.getDrawable(
            context, if (profile.gender == Gender.FEMALE) {
                R.drawable.ui_ic_avatar_default_female
            } else {
                R.drawable.ui_ic_avatar_default_male
            }
        ),
        null,
        null,
        object : LoadImgCallback {
            override fun onSuccess(url: String) {
                Log.d("LoadAvatar", "success $url")
            }

            override fun onFailed(url: String, exception: Exception) {
                Log.e("LoadAvatar", "failed $url, $exception")
            }
        }
    )
}

fun ImageView.loadImgFromNetWork(url: String, @DrawableRes placeHolder: Int, transformation: BitmapTransformation) {
    loadImgFromNetWork(
        url,
        ContextCompat.getDrawable(context, placeHolder),
        transformation
    )
}

fun ImageView.loadImgFromNetWork(
    url: String,
    @DrawableRes placeHolder: Int,
    @DrawableRes error: Int,
    loadImgCallback: LoadImgCallback
) {
    loadImgFromNetWork(
        url,
        ContextCompat.getDrawable(context, placeHolder),
        ContextCompat.getDrawable(context, error),
        null,
        loadImgCallback
    )
}

fun ImageView.loadImgFromNetWork(
    url: String,
    placeHolder: Drawable?,
    loadImgCallback: LoadImgCallback
) {
    loadImgFromNetWork(url, placeHolder, null, null, loadImgCallback)
}

fun ImageView.loadImgFromNetWork(
    url: String,
    placeHolder: Drawable?,
    transformation: BitmapTransformation
) {
    loadImgFromNetWork(url, placeHolder, null, transformation, null)
}

private fun ImageView.loadImgFromNetWork(
    url: String,
    placeHolder: Drawable? = null,
    error: Drawable? = null,
    transformation: BitmapTransformation? = null,
    loadImgCallback: LoadImgCallback?
) {
    if (!isContextAvaliable(context)) {
        Log.d(TAG, "context is invalid")
        return
    }

    RequestOptions()
        .placeholder(placeHolder)
        .error(error)
        .apply {
            transformation?.let {
                transform(it)
            }
        }
        .also {
            Glide.with(this).asDrawable()
                .load(url)
                .apply(it)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadImgCallback?.onFailed(url, e ?: IllegalStateException("glide do not now why"))
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadImgCallback?.onSuccess(url)
                        return false
                    }
                })
                .into(this)
        }
}

/**
 * 从网络 URL 获取图片并通过 Picasso 加载
 * @param url String? null 的时候，就加载 placeholder
 * @param placeholderResId Int 占位图资源文件，小于0时不加载
 * @param scaleType ScaleType ImageView 缩放模式
 */
fun ImageView.loadImgFromNetWork(
    url: String?,
    placeholderResId: Int = -1,
    scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER
) {
    if (!isContextAvaliable(context)) {
        Log.d(TAG, "context is invalid")
        return
    }

    RequestOptions()
        .placeholder(placeholderResId)
        .apply {
            when (scaleType) {
                ImageView.ScaleType.CENTER_CROP -> centerCrop()
                ImageView.ScaleType.CENTER_INSIDE -> centerInside()
                else -> {
                    centerCrop()
                }
            }
        }.also {
            Glide.with(this).load(url).apply(it).into(this)
        }
}

private class LogGlideListener(private val path: String) : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        Log.e(TAG, "failed,path:$path", e)
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        Log.d(TAG, "success,path:$path")
        return false
    }
}

interface LoadImgCallback {
    fun onSuccess(url: String)

    fun onFailed(url: String, exception: Exception)
}