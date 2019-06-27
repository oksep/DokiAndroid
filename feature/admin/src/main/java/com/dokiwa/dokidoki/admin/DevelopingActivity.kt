package com.dokiwa.dokidoki.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin

class DevelopingActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context): Boolean {
            // context.startActivity(Intent(context, DevelopingActivity::class.java))
            IHomePlugin.get().launchHomeActivity(context as Activity)
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developing)
        supportFragmentManager.beginTransaction().add(
            R.id.root,
            ITimelinePlugin.get().obtainHomeTimelineFragment()
        ).commit()
    }
}
