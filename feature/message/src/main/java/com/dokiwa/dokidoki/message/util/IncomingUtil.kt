package com.dokiwa.dokidoki.message.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.message.R

fun vibrateIncomingMsg(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

fun soundIncomingMsg(context: Context) {
    MediaPlayer.create(AppCenter.get().context, R.raw.msg_new_income).apply {
        isLooping = false
        setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
        setOnCompletionListener {
            release()
        }
        start()
    }
}