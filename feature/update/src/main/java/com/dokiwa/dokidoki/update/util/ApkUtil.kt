package com.dokiwa.dokidoki.update.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

object ApkUtil {
    fun installApk(context: Context, apkPath: String) {
        if (apkPath.isEmpty()) return

        val file = File(apkPath)

        val intent = Intent(Intent.ACTION_VIEW).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val apkUri: Uri = try {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                FileProvider.getUriForFile(context, context.packageName, file)
            } catch (e: Exception) {
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
                Log.w("ApkUtil", "get url failed from file", e)
                Uri.fromFile(file)
            }
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }

        context.startActivity(intent)
    }
}
