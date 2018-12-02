package com.dokiwa.dokidoki

import android.os.Bundle
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribe
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import io.reactivex.Single
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        delayToHome()
    }

    private fun delayToHome() {
        FeaturePlugin.get(ILoginPlugin::class.java).launchLoginActivity(this)
        fun toHome() {
            FeaturePlugin.get(IHomePlugin::class.java).launchHomeActivity(this)
            finish()
        }
        Single.timer(1000 * 1, TimeUnit.MILLISECONDS)
            .subscribe(
                this,
                Consumer { toHome() },
                Consumer { toHome() }
            )
    }
}