package com.dokiwa.dokidoki.profile.create.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // 性别
    val gener = MutableLiveData<Int>()

    // 头像
    val avatar = MutableLiveData<String>()

    // 昵称
    val nick = MutableLiveData<String>()

    // 城市
    val city = MutableLiveData<String>()

    // 生日
    val birth = MutableLiveData<String>()
}