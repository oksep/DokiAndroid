package com.dokiwa.dokidoki.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.BaseActivity

class DevelopingActivity : BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, DevelopingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developing)
    }
}
