package com.dokiwa.dokidoki.timeline.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Septenary on 2019-07-02.
 */

class PayLoadViewModel : ViewModel() {
    val content: MutableLiveData<String> = MutableLiveData()
    val pictures: MutableLiveData<MutableList<CreateTimelineActivity.SelectImage>> = MutableLiveData()
    val locationMeta: MutableLiveData<LocationMeta> = MutableLiveData()
}

class LocationMeta(
    val name: String,
    val latitude: Double,
    val longitude: Double
)