package com.dokiwa.dokidoki.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.feed.IFeedPlugin

class DevelopingActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, DevelopingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developing)
        supportFragmentManager.beginTransaction().add(
            R.id.root,
            IFeedPlugin.get().obtainHomeFeedFragment()
        ).commit()
    }
}
