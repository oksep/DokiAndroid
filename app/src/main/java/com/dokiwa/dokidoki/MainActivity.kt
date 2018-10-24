package com.dokiwa.dokidoki

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text).text = packageName
    }

    fun openAdminActivity(view: View) {
        IAdminPlugin.get().launchAdmin(this)
    }

    fun openLoginActivity(view: View) {
        ILoginPlugin.get().launchLoginActivity(this)
    }
}
