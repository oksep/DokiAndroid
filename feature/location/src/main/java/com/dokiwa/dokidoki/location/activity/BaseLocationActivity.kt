package com.dokiwa.dokidoki.location.activity

import androidx.appcompat.app.AlertDialog
import com.amap.api.location.AMapLocation
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.location.Log
import com.dokiwa.dokidoki.location.R
import com.dokiwa.dokidoki.location.helper.LocationHelper
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
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

    private var cacheAMapLocation: AMapLocation? = null

    private val holder = hashSetOf<SingleSubject<AMapLocation>>()

    fun requestLocation(): Single<AMapLocation> {
        return if (cacheAMapLocation != null) {
            Single.just(cacheAMapLocation)
        } else {
            SingleSubject.create<AMapLocation>().run {
                doOnSubscribe {
                    holder.add(this)
                    loadLocationImplWithPermissionCheck()
                }
            }
        }
    }

    @NeedsPermission(ACL, AFL, WES, RES, RPS)
    fun loadLocationImpl() {
        LocationHelper.getLocation(this, lifecycle, ::onGetLocation)
    }

    private fun onGetLocation(location: AMapLocation?) {

        cacheAMapLocation = if (location?.errorCode == 0) location else null

        val closure = { emitter: SingleSubject<AMapLocation> ->
            if (location?.errorCode == 0) {
                emitter.onSuccess(location)
            } else {
                emitter.onError(
                    LocationHelper.LocationException(LocationHelper.toMessage(location))
                )
            }
        }
        holder.forEach { closure(it) }
        holder.clear()
    }

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