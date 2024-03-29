package com.dokiwa.dokidoki.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin

class DevelopingActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context): Boolean {
            // context.startActivity(Intent(context, DevelopingActivity::class.java))
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ILoginPlugin.get().ensureLogin(this)
        setContentView(R.layout.activity_developing)
        supportFragmentManager.beginTransaction().add(
            R.id.root,
            IRelationshipPlugin.get().getDevFragment()
        ).commit()
    }
}
