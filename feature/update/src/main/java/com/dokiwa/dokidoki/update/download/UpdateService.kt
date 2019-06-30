package com.dokiwa.dokidoki.update.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import com.dokiwa.dokidoki.center.base.service.BaseLocalService
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.util.now
import com.dokiwa.dokidoki.update.R
import com.dokiwa.dokidoki.update.api.UpdateInfo
import com.dokiwa.dokidoki.update.util.ApkUtil
import java.io.File

internal const val EXTRA_UPDATE_INFO = "extra.update_info"

internal class UpdateService : BaseLocalService() {

    private var downloadInfo: DownloadInfo? = null

    override fun onCreate() {
        super.onCreate()
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onDestroy() {
        unregisterReceiver(onDownloadComplete)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getParcelableExtra<UpdateInfo>(EXTRA_UPDATE_INFO)?.let {
            downloadApk(it)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun downloadApk(updateInfo: UpdateInfo) {

        if (downloadInfo != null) return

        downloadDir().mkdir()

        val url = updateInfo.url

        val fileName = "dokidoki-${updateInfo.versionName}-${now()}.apk"

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("DokiDoki")
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            setAllowedOverRoaming(false)
            setMimeType("application/vnd.android.package-archive")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val downloadID = downloadManager.enqueue(request)

        this.downloadInfo = DownloadInfo(url, fileName, downloadID)

        toast(R.string.update_downloading)
    }

    private fun downloadDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadInfo?.downloadID == id) {
                toast(R.string.update_download_complete)
                ApkUtil.installApk(this@UpdateService, downloadDir().path + File.separator + downloadInfo!!.fileName)
                downloadInfo = null
            }
        }
    }

    private data class DownloadInfo(val url: String, val fileName: String, val downloadID: Long)
}
