package com.dokiwa.dokidoki.home.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * Created by Septenary on 2018/12/31.
 */
class SharedViewModel : ViewModel() {
    private val content = MutableLiveData<String>()

    fun setContent(conent: String) {
        this.content.value = conent
    }

    fun getContent() = content
}