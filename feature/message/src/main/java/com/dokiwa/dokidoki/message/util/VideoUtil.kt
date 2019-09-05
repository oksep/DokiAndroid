package com.dokiwa.dokidoki.message.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.File

/**
 * Created by Septenary on 2019-09-05.
 */
private fun getVideoMediaPlayer(context: Context, file: File): MediaPlayer? {
    try {
        return MediaPlayer.create(context, Uri.fromFile(file))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun checkVideo(context: Context, file: File): Triple<Long, Int, Int> {
    val mediaPlayer = getVideoMediaPlayer(context, file)
    val duration = (mediaPlayer?.duration ?: 0).toLong()
    val height = mediaPlayer?.videoHeight ?: 0
    val width = mediaPlayer?.videoWidth ?: 0
    return Triple(duration, width, height)
}