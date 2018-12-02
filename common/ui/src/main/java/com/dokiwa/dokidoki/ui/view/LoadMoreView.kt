package com.dokiwa.dokidoki.ui.view

import com.dokiwa.dokidoki.ui.R
import com.chad.library.adapter.base.loadmore.LoadMoreView as QuickLoadMoreView

class LoadMoreView : QuickLoadMoreView() {

    override fun getLayoutId() = R.layout.ui_view_loading_more

    override fun getLoadingViewId() = R.id.loading

    override fun getLoadEndViewId() = R.id.noMore

    override fun getLoadFailViewId() = R.id.failedAndRetry
}