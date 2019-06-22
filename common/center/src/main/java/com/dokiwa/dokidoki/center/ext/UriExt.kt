package com.dokiwa.dokidoki.center.ext

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.dokiwa.dokidoki.center.plugin.web.IWebPlugin

private const val SCHEMA_DOKI = "dokidoki"

/**
 * 检查 uri 是否能启动在 AndroidManifest 声明的 Activity
 *
 * @param dryRun true(只检查, 不做真正启动), false(检查并启动)
 * @return 返回是否能够启动
 */
fun Uri.resolveDeepLink(context: Context, dryRun: Boolean = false): Boolean {
    val schema = this.scheme?.toLowerCase()
    if (schema == "http" || schema == "https") {
        if (!dryRun) {
            IWebPlugin.get().launchWebActivity(context, this.toString())
        }
        return true
    } else if (schema == SCHEMA_DOKI) {
        val intent = Intent(Intent.ACTION_VIEW, this)
        val matchedAct = context.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if ((matchedAct?.size ?: 0) > 0) {
            if (!dryRun) {
                context.startActivity(intent)
            }
            return true
        }
    }
    return false
}

fun String.toUriAndResolveDeepLink(context: Context, dryRun: Boolean): Boolean {
    return Uri.parse(this).resolveDeepLink(context, dryRun)
}