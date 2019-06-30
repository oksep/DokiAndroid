package com.dokiwa.dokidoki.location

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PoiAdapter : BaseQuickAdapter<Location, BaseViewHolder>(R.layout.view_item_location, null) {
    override fun convert(helper: BaseViewHolder, profile: Location) {

    }
}

class Location(val title: String, val subTitle: String)