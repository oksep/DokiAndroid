package com.dokiwa.dokidoki

import android.os.Bundle
import android.os.Handler
import androidx.core.os.HandlerCompat
import com.dokiwa.dokidoki.center.activity.BaseActivity

class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        HandlerCompat.postDelayed(Handler(), {
            HomeActivity.launch(this@LaunchActivity)
            finish()
        }, null, 1000)
    }
}
