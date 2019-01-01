package com.dokiwa.dokidoki.web

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.web.IWebPlugin

/**
 * Created by Septenary on 2019/1/2.
 */
class WebPlugin : IWebPlugin {
    override fun launchWebActivity(context: Context, url: String) {
        WebViewActivity.launch(context, url)
    }
}