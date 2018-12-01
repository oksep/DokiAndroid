package com.dokiwa.dokidoki.center.base.activity

import android.os.Bundle

/**
 * Created by Septenary on 2018/12/1.
 */
open class TranslucentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.translucentStatusBar()
    }
}