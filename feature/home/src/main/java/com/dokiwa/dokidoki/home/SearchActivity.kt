package com.dokiwa.dokidoki.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.toast

class SearchActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Activity) {
            context.startActivity(Intent(context, SearchActivity::class.java))
            // context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        toast("to search page")
    }
}