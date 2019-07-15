package com.dokiwa.dokidoki

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribe
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import io.reactivex.Single
import io.reactivex.functions.Consumer
import permissions.dispatcher.*
import java.util.concurrent.TimeUnit
import android.Manifest.permission.ACCESS_COARSE_LOCATION as ACL
import android.Manifest.permission.ACCESS_FINE_LOCATION as AFL
import android.Manifest.permission.BLUETOOTH as BLT
import android.Manifest.permission.CAMERA as CMR
import android.Manifest.permission.READ_EXTERNAL_STORAGE as RES
import android.Manifest.permission.READ_PHONE_STATE as RPS
import android.Manifest.permission.RECORD_AUDIO as RA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE as WES

@RuntimePermissions
class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        if (IAdminPlugin.get().launchDevelopingPage(this)) {
            finish()
        } else {
            // delayToHomeWithPermissionCheck()
            delayToHome()
        }
    }

    @NeedsPermission(value = [ACL, AFL, RES, RPS, WES, CMR, RA, BLT])
    fun delayToHome() {
        fun toHome() {
            IHomePlugin.get().launchHomeActivity(this)
            finish()
        }
        Single.timer(800 * 1, TimeUnit.MILLISECONDS)
            .subscribe(
                this,
                Consumer { toHome() },
                Consumer { toHome() }
            )
    }

    ///////////////////////////////////////////
    // permissions
    ///////////////////////////////////////////
    @OnShowRationale(value = [ACL, AFL, RES, RPS, WES, CMR, RA, BLT])
    fun showRationalPermissions(request: PermissionRequest) {
        showRationaleDialog(R.string.center_permission_require, request)
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setTitle(R.string.tip)
            .setPositiveButton(R.string.confirm) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.cancel) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }

    @OnNeverAskAgain(value = [ACL, AFL, RES, RPS, WES, CMR, RA, BLT])
    fun onPermissionAskAgain() {
        toast(R.string.center_permission_require)
    }

    @OnPermissionDenied(value = [ACL, AFL, RES, RPS, WES, CMR, RA, BLT])
    fun onPermissionDenied() {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}