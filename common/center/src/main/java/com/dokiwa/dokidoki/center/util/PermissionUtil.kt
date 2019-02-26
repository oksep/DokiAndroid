package com.dokiwa.dokidoki.center.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pub.devrel.easypermissions.EasyPermissions


object PermissionUtil {

    const val cameraPermission = Manifest.permission.CAMERA

    fun geBasicPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO
        )
    }

    /**
     * @return Whether has permission in `deniedPerms` is in NEVER ASK AGAIN status.
     */
    fun checkDeniedPermissionsNeverAskAgain(
        activity: Activity,
        @StringRes message: Int,
        @StringRes positiveButton: Int,
        @StringRes negativeButton: Int,
        deniedPerms: List<String>,
        positiveAction: DialogInterface.OnClickListener,
        negativeAction: DialogInterface.OnClickListener
    ): Boolean {
        for (perm in deniedPerms) {
            // never ask again.
            if (!EasyPermissions.hasPermissions(activity, perm) && !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    perm
                )
            ) {

                showDialog(
                    activity, message, positiveButton,
                    negativeButton, positiveAction, negativeAction
                )

                return true
            }
        }

        return false
    }

    private fun showDialog(
        activity: Activity,
        @StringRes message: Int,
        @StringRes positiveButton: Int,
        @StringRes negativeButton: Int,
        positiveAction: DialogInterface.OnClickListener?,
        negativeAction: DialogInterface.OnClickListener
    ) {

        val dialog = AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
                positiveAction?.onClick(dialog, which)
            }
            .setNegativeButton(negativeButton, negativeAction)
            .create()
        dialog.show()
    }

    private fun hasPermissionInManifest(context: Context, permissionName: String): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo = context.packageManager
                .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermissions = packageInfo.requestedPermissions
            if (declaredPermissions != null) {
                for (p in declaredPermissions) {
                    if (p == permissionName) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // ignored
        }

        return false
    }

    /**
     * 检测是否需要申请相机权限
     *
     * @param context 上下文
     * @return True: 需要申请 False: 不需要申请
     */
    fun shouldRequestForCameraPermission(context: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = Manifest.permission.CAMERA
            return hasPermissionInManifest(context, permission) && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        }
        return false
    }
}
