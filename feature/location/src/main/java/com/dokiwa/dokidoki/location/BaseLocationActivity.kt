package com.dokiwa.dokidoki.location

import androidx.appcompat.app.AlertDialog
import com.amap.api.location.AMapLocation
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import permissions.dispatcher.*
import android.Manifest.permission.ACCESS_COARSE_LOCATION as ACL
import android.Manifest.permission.ACCESS_FINE_LOCATION as AFL
import android.Manifest.permission.READ_EXTERNAL_STORAGE as RES
import android.Manifest.permission.READ_PHONE_STATE as RPS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE as WES

/**
 * Created by Septenary on 2019-06-30.
 */
@RuntimePermissions
abstract class BaseLocationActivity : TranslucentActivity() {

    companion object {
        private const val TAG = "BaseLocationActivity"
    }

    fun requsetLocation() {
        loadLocationImplWithPermissionCheck()
    }

    @NeedsPermission(ACL, AFL, WES, RES, RPS)
    fun loadLocationImpl() {
        LocationHelper.getLocation(this, lifecycle, ::onGetLocation)
    }

    abstract fun onGetLocation(location: AMapLocation)

    ///////////////////////////////////////////
    // permissions
    ///////////////////////////////////////////
    @OnShowRationale(ACL, AFL, WES, RES, RPS)
    fun showRationalPermissions(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setTitle(R.string.tip)
            .setPositiveButton(R.string.confirm) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.cancel) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(R.string.center_permission_require)
            .show()
    }

    @OnNeverAskAgain(ACL, AFL, WES, RES, RPS)
    fun onPermissionAskAgain() {
        toast(R.string.center_permission_require)
        Log.w(TAG, "onPermissionAskAgain")
    }

    @OnPermissionDenied(ACL, AFL, WES, RES, RPS)
    fun onPermissionDenied() {
        Log.w(TAG, "onPermissionDenied")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}