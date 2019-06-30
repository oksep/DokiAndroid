package com.dokiwa.dokidoki.update.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.IntRange
import androidx.core.app.NotificationCompat
import com.dokiwa.dokidoki.update.R

/**
 * Created by Septenary on 2018/8/22.
 */
class UpdateNotificator(private val context: Context) {

    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    private var builder: NotificationCompat.Builder? = null

    companion object {
        private const val CHANNEL_ID = "channel.update.id"
        private const val CHANNEL_NAME = "channel.update.name"
        private const val NOTIFICATION_ID = 0x0001
    }

    private fun ensureBuilderCreated() {
        if (builder == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                manager?.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "Darwin latest apk download"
                    }
                )
            }
            builder = NotificationCompat.Builder(context,
                CHANNEL_ID
            )
                .setContentTitle(context.getString(R.string.update_downloading_file, 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
        }
    }

    fun notifyProgress(@IntRange(from = 0, to = 100) progress: Int): Notification? {
        ensureBuilderCreated()
        builder?.setContentTitle(context.getString(R.string.update_downloading_file, progress))
            ?.setProgress(100, progress, false)
            ?.setOnlyAlertOnce(true)
        val nf = builder?.build()
        if (nf != null) {
            manager?.notify(NOTIFICATION_ID, builder?.build())
        }
        return nf
    }
}