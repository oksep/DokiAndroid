package com.dokiwa.dokidoki.center.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Septenary on 2019-06-07.
 */
@SuppressLint("SimpleDateFormat")
fun String.birthDayToAge(): Int {
    fun getAgeByBirth(birthday: Date): Int {
        var age: Int
        try {
            val now = Calendar.getInstance()
            now.time = Date()// 当前时间

            val birth = Calendar.getInstance()
            birth.time = birthday

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1
                }
            }
            return age
        } catch (e: Exception) {//兼容性更强,异常后返回数据
            return 0
        }
    }
    return getAgeByBirth(SimpleDateFormat("yyyyMMdd").parse(this))
}

@SuppressLint("SimpleDateFormat")
fun Int.toLastActiveTime(): String {
    val systemStamp = System.currentTimeMillis() / 1000L
    val userStamp = this.toLong()
    val distanceInteger = systemStamp - userStamp

    return when {
        distanceInteger / (60 * 60 * 24 * 7) >= 1 -> {
            // SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(userStamp))
            ""
        }
        distanceInteger / (60 * 60 * 24 * 1) >= 1 -> {
            val d = distanceInteger / (60 * 60 * 24 * 1)
            "$d${"天前"}"
        }
        distanceInteger / (60 * 60) >= 1 -> {
            val h = distanceInteger / (60 * 60)
            "$h${"小时前"}"
        }
        distanceInteger / (60 * 30) >= 1 -> {
            val m = distanceInteger / (60)
            "$m${"分钟前"}"
        }
        else -> "刚刚"
    }
}