package com.dokiwa.dokidoki.home.pages

import androidx.lifecycle.ViewModel
import com.dokiwa.dokidoki.home.api.model.FeedPage
import com.dokiwa.dokidoki.home.pages.feed.FeedFilter

/**
 * Created by Septenary on 2018/12/31.
 */
class SharedViewModel : ViewModel() {

    val feedPageViewModel = FeedViewModel()

}

data class FeedViewModel(
    val feedPages: MutableList<FeedPage> = mutableListOf(),
    val feedFilter: FeedFilter = FeedFilter()
)