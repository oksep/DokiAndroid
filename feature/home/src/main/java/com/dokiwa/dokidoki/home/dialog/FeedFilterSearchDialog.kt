package com.dokiwa.dokidoki.home.dialog

import android.content.Context
import com.dokiwa.dokidoki.center.dialog.BaseDialog
import com.dokiwa.dokidoki.home.R

/**
 * Created by Septenary on 2019-06-07.
 */
class FeedFilterSearchDialog(context: Context) : BaseDialog(context, R.style.NormalDialog) {
    init {
        setContentView(R.layout.dialog_feed_filter_search)
        fitNotch()
    }
}