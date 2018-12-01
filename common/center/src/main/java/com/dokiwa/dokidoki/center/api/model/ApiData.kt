package com.dokiwa.dokidoki.center.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Septenary on 2018/12/1.
 *
 * api 统一返回数据格式
 * ```
 *  {
 *      "status": {
 *          "code": 200000,
 *          "err_msg": "ok"
 *      },
 *      "time": 1532530115,
 *      "data": {
 *      },
 *      "action": {
 *          "to": "dokidoki://dokiwa.com/me/bind_phone"
 *      }
 * }
 * ```
 */
data class ApiData<T>(
    val status: Status,
    val time: Int,
    val data: T,
    val action: Action?
) {
    data class Status(
        @SerializedName("err_msg")
        val errorMessage: String?,
        val code: Int
    )

    data class Action(
        val to: String
    )
}