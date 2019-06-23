package com.dokiwa.dokidoki.feed

import androidx.fragment.app.Fragment
import com.dokiwa.dokidoki.center.plugin.feed.IFeedPlugin
import com.dokiwa.dokidoki.feed.home.FeedFragment

/**
 * Created by Septenary on 2018/10/24.
 */
class FeedPlugin : IFeedPlugin {
    override fun obtainHomeFeedFragment(): Fragment {
        return FeedFragment()
    }
}