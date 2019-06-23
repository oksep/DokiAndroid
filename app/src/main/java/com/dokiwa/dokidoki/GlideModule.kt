package com.dokiwa.dokidoki

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

private const val TAG = "GlideModule"

@GlideModule
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val uncaughtThrowableStrategy = GlideExecutor.UncaughtThrowableStrategy { throwable ->
            Log.e(TAG, "uncaught throwable", throwable)
        }
        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(uncaughtThrowableStrategy))
        builder.setSourceExecutor(GlideExecutor.newSourceExecutor(uncaughtThrowableStrategy))
        builder.setAnimationExecutor(
            GlideExecutor.newAnimationExecutor(GlideExecutor.calculateBestThreadCount(), uncaughtThrowableStrategy)
        )

        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig())
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(android.util.Log.DEBUG)
        }
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}