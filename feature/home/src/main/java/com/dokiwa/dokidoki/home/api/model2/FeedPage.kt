package com.dokiwa.dokidoki.home.api.model2

import com.dokiwa.dokidoki.center.api.model.IApiModel
import com.dokiwa.dokidoki.center.api.model.IApiModelPage
import com.dokiwa.dokidoki.center.ext.toRetrofitQueryMap
import com.google.gson.annotations.SerializedName

data class FeedPage(
    @SerializedName("feed_list")
    val feedList: List<Feed>,
    val next: String?
) : IApiModel, IApiModelPage {
    override val nextQ: Map<String, String?>
        get() = this.next?.toRetrofitQueryMap() ?: mapOf()
}
