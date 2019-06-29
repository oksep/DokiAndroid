package com.dokiwa.dokidoki.timeline.comment

import com.dokiwa.dokidoki.timeline.api.TimelinePage

data class TimelineCommentViewModel(
    val timelinePages: MutableList<TimelinePage> = mutableListOf()
)