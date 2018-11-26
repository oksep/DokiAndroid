package com.dokiwa.dokidoki.center.api

interface ApiData

//data class Action(val to: String)
//
//data class Status(val code: Int, val err_msg: String?)
//
//data class User(val name: String, val address: String)
//
//fun <T> get(clz: Class<T>) {
//
//}

//fun test() {
//    get(clz = ApiResponse<User>::class.java)
//}
//
//data class ServiceCall<out T>(val result: T?, val exception: String?, val pagination: String?, val stringResult: String?)
//data class SurveyListModel(val text: String)
//
//val type: Type = object : TypeToken<ServiceCall<SurveyListModel>>() {}.type
//val r = Gson().fromJson<ServiceCall<SurveyListModel>>("", type).result
//
//val result = Gson().fromJson("", ServiceCall<SurveyListModel>::class.java).result
//
//class Token : TypeToken<List<Int>>()
//val x: List<Int> = Gson().fromJson(Gson().toJson(arrayOf(1)), Token().type)

// status code
// 200000	成功
// 400000	因为参数错误请求失败，客户端需要将 err_msg 中的信息展示给用户。
// 400001	signature 错误。
// 400100	用户被封禁。
// 401000	授权失败，客户端需要重新登录。
// 401xxx	其他授权失败情况。
// 401002	用户的第三方账号未绑定手机。客户端需引导用户到手机号登陆页面，并在手机号提交时同时提交用户第三方账号相关参数。参考 @使用短信 注册/登录
// 50xxxx	服务器异常导致的失败。客户端需要提示用户稍后重试，此时不应该展示 err_msg 中的错误信息。
