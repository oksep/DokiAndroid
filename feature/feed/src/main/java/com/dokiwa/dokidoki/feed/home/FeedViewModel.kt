package com.dokiwa.dokidoki.feed.home

import com.dokiwa.dokidoki.feed.api.FeedPage

data class FeedViewModel(
    val feedPages: MutableList<FeedPage> = mutableListOf(),
    val feedFilter: FeedFilter = FeedFilter()
)
