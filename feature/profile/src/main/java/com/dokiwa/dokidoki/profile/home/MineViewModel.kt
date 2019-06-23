package com.dokiwa.dokidoki.profile.home

import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.profile.api.RelationCount

/**
 * Created by Septenary on 2019-06-23.
 */
data class MineViewModel(
    var profileWrap: UserProfileWrap? = null,
    var countsModel: CountsViewModel = CountsViewModel()
)

data class CountsViewModel(
    var relationCount: RelationCount? = null,
    var timeLineCount: Int? = null
)