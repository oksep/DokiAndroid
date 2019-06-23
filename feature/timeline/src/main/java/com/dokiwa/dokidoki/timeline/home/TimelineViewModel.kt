package com.dokiwa.dokidoki.timeline.home

import com.dokiwa.dokidoki.timeline.api.TimelinePage

data class TimelineViewModel(
    val timelinePages: MutableList<TimelinePage> = mutableListOf()
)