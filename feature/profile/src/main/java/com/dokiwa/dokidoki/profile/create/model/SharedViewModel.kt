package com.dokiwa.dokidoki.profile.create.model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // 性别 @see Gender
    val gender = MutableLiveData<Int>()

    // 生日 格式为 yyyymmdd，如 19901201
    val birth = MutableLiveData<String>()

    // 身高 单位 cm
    val height = MutableLiveData<Int>()

    // 城市 city_code
    val city = MutableLiveData<String>()

    // 昵称
    val nick = MutableLiveData<String>()

    // 头像 URL，上传规则 @see common.md 通用接口-上传图片
    val avatar = MutableLiveData<Uri>()

    override fun toString(): String {
        return """
            Mode ->
            gender: ${gender.value}
            birth: ${birth.value}
            height: ${height.value}
            city: ${city.value}
            nick: ${nick.value}
            avatar: ${avatar.value}
        """.trimIndent()
    }
}