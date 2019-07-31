package com.dokiwa.dokidoki.message.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.appcompat.app.AlertDialog
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.message.R

/**
 * Created by Septenary on 2019/7/30.
 */
class KickOutDialog : TranslucentActivity() {

    companion object {

        private const val TAG = "KickOutDialog"

        fun launch(context: Context) {
            context.startActivity(
                Intent(context, KickOutDialog::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
            (context as? Activity)?.overridePendingTransition(
                R.anim.ui_anim_none,
                R.anim.ui_anim_none
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(Space(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.tip)
            .setMessage("登录过期，请重新登录")
            .setCancelable(false)
            .setPositiveButton(R.string.confirm) { d, _ ->
                ILoginPlugin.get().logOut(this)
                d.cancel()
                finish()
            }
            .show()
    }

    override fun finish() {
        overridePendingTransition(
            R.anim.ui_anim_none,
            R.anim.ui_anim_none
        )
        super.finish()
    }

}