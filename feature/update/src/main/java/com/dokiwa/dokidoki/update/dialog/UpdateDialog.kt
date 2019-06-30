package com.dokiwa.dokidoki.update.dialog

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.update.R
import com.dokiwa.dokidoki.update.UpdateManager
import com.dokiwa.dokidoki.update.api.UpdateInfo
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

/**
 * Created by Septenary on 2018/11/6.
 */
@RuntimePermissions
class UpdateDialog : TranslucentActivity() {

    companion object {

        private const val EXTRA_UPDATE_INFO = "extra.update_info"

        fun show(context: Activity, updateInfo: UpdateInfo) {
            context.startActivity(
                Intent(context, UpdateDialog::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(EXTRA_UPDATE_INFO, updateInfo)
                }
            )
            context.overridePendingTransition(
                R.anim.ui_anim_none,
                R.anim.ui_anim_none
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(Space(this))

        val updateInfo = intent.getParcelableExtra<UpdateInfo>(EXTRA_UPDATE_INFO) ?: return finish()

        if (updateInfo.forceUpdate) {
            showForceUpdateDialog()
        } else {
            showEasyUpdateDialog(updateInfo)
        }
    }

    override fun finish() {
        overridePendingTransition(
            R.anim.ui_anim_none,
            R.anim.ui_anim_none
        )
        super.finish()
    }

    private fun showEasyUpdateDialog(updateInfo: UpdateInfo) {
        AlertDialog.Builder(this)
            .setTitle(R.string.update_dialog_tip)
            .setMessage(updateInfo.changeLog)
            .setCancelable(false)
            .setPositiveButton(R.string.update_force_go) { d, _ ->
                doUpdateWithPermissionCheck()
                d.cancel()
                finish()
            }
            .setNegativeButton(R.string.update_force_quit) { d, _ ->
                d.cancel()
                finish()
            }
            .show()
    }

    private fun showForceUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.update_force_tip)
            .setMessage(R.string.update_force_message)
            .setCancelable(false)
            .setPositiveButton(R.string.update_force_go) { d, _ ->
                doUpdateWithPermissionCheck()
                d.cancel()
                gotoHome()
            }
            .setNegativeButton(R.string.update_force_quit) { d, _ ->
                d.cancel()
                gotoHome()
            }
            .show()
    }

    private fun gotoHome() {
        finish()
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        })
    }

    @NeedsPermission(value = [READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE])
    fun doUpdate() {
        UpdateManager.doUpdate(this@UpdateDialog)
    }
}