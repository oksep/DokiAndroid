package com.dokiwa.dokidoki.message.home

import android.content.Context
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.util.LineDivider

/**
 * Created by Septenary on 2019-06-23.
 */
class LineDivider(context: Context) : LineDivider(
    context.resources,
    R.color.dd_window,
    R.dimen.home_message_divider_height,
    R.dimen.home_message_divider_left,
    R.dimen.home_message_divider_right
)
