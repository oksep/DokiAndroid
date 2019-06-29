package com.dokiwa.dokidoki.timeline.comment

import android.content.Context
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.ui.util.LineDivider

/**
 * Created by Septenary on 2019-06-23.
 */
class LineDivider(context: Context) : LineDivider(
    context.resources,
    R.color.dd_window,
    R.dimen.timeline_comment_divider_height,
    R.dimen.timeline_comment_divider_margin,
    R.dimen.timeline_comment_divider_margin
) {
    override fun ensureDraw(position: Int): Boolean {
        return position >= 2
    }
}