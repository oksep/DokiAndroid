package com.dokiwa.dokidoki.center.base.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

abstract class BaseLocalService : Service() {

    private val mBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        fun <T : BaseLocalService> getService(): T? {
            return this@BaseLocalService as? T
        }
    }
}
