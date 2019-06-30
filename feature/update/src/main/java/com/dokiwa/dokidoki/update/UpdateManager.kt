package com.dokiwa.dokidoki.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.update.api.UpdateApi
import com.dokiwa.dokidoki.update.api.UpdateInfo
import com.dokiwa.dokidoki.update.api.needUpdate
import com.dokiwa.dokidoki.update.dialog.UpdateDialog
import com.dokiwa.dokidoki.update.download.EXTRA_UPDATE_INFO
import com.dokiwa.dokidoki.update.download.UpdateService
import io.reactivex.Single

/**
 * Created by Septenary on 2019/1/24.
 */
object UpdateManager {

    private const val TAG = "UpdateManager"

    private var updateInfo: UpdateInfo? = null

    fun doUpdate(context: Context) {
        updateInfo?.let {
            context.startService(
                Intent(context, UpdateService::class.java).putExtra(
                    EXTRA_UPDATE_INFO, it
                )
            )
        }
    }

    private fun checkUpdate(force: Boolean = false): Single<UpdateInfo> {
        return if (force || updateInfo == null) {
            Api.get(UpdateApi::class.java, "https://gitee.com")
                .checkAppUpdate()
                .map { it.version }
                .doOnSuccess {
                    updateInfo = it
                }
        } else {
            Single.just(updateInfo)
        }
    }

    fun checkUpdate(context: Activity) {
        checkUpdate(true)
            .subscribeApi(context as? CompositeDisposableContext, {
                Log.d(TAG, "get versions updateInfo -> $it")
                if (it.needUpdate(context)) {
                    UpdateDialog.show(context, it)
                }
            }, {
                Log.e(TAG, "get versions failed", it)
            })
    }
}