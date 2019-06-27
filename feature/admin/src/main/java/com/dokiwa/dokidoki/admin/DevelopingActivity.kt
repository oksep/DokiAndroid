package com.dokiwa.dokidoki.admin

import android.content.Context
import android.os.Bundle
import com.dokiwa.dokidoki.admin.api.AdminApi
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import io.reactivex.functions.Consumer

class DevelopingActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context): Boolean {
//            context.startActivity(Intent(context, DevelopingActivity::class.java))
            Api.get(AdminApi::class.java).getMineProfile().subscribe(Consumer {
                IProfilePlugin.get().launchEditProfileActivity(context, it.profile)
            }).also { }
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
