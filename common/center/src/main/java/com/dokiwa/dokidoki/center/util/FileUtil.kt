package com.dokiwa.dokidoki.center.util

import android.content.Context
import android.net.Uri
import com.dokiwa.dokidoki.center.Log
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import java.io.InputStream

/**
 * Created by Septenary on 2019-06-22.
 */
private const val TAG = "FileUtil"

fun Uri.toUploadFileSingle(context: Context): Single<String> {
    return Single.create<String> {
        try {
            val path = this.toUploadFile(context)
            it.onSuccess("file://$path")
        } catch (e: Exception) {
            Log.w(TAG, "toUploadFile error", e)
            throw e
        }
    }.onErrorReturn { "" }
}

fun Uri.toUploadFileObservable(context: Context): Observable<String> {
    return toUploadFileSingle(context).toObservable()
}

fun Uri.toUploadFile(context: Context): String {
    return try {
        val saveDir = File(context.cacheDir, "upload").ensureFileDir()
        val stream = context.contentResolver.openInputStream(this)
        val tmp = File.createTempFile(now(), ".jpg", saveDir)
        tmp.copyInputStreamToFile(stream)
        tmp.absolutePath
    } catch (e: Exception) {
        throw e
    }
}

fun File.ensureFileDir(): File {
    if (!this.exists()) {
        mkdirs()
    }
    return this
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    inputStream.use { input ->
        this.outputStream().use { fileOut ->
            input.copyTo(fileOut)
        }
    }
}