package com.dokiwa.dokidoki.center.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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