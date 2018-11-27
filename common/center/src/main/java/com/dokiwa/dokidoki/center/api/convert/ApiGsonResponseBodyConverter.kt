package com.dokiwa.dokidoki.center.api.convert

import android.text.TextUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Converter

import java.io.IOException

/**
 * Created by Septenary on 2018/11/27.
 */
class ApiGsonResponseBodyConverter(val gson: Gson, val clazz: Class<*>) : Converter<ResponseBody, List<String>> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): List<String>? {
        val json = value.string()
        val obj = gson.fromJson(json, clazz)

        //第一步将 ResponseBody
        if (TextUtils.isEmpty(json)) return null
        //第二步将 String 转换为  BaseBean<String>
        try {
//            val jsonObject = JSONObject(json)
//            val code = GsonUtils.getJsonInt(jsonObject, "code")
//            val message = GsonUtils.getJsonString(jsonObject, "message")
//            val action = GsonUtils.getJsonString(jsonObject, "action")
//            val jsonData = GsonUtils.getJsonObject(jsonObject, "data")
//            var data: String? = null
//            if (jsonData != null) {
//                data = jsonData.toString()
//            }
//            val baseBean = BaseBean<String>()
//            baseBean.setCode(code)
//            baseBean.setMsg(message)
//            baseBean.setData(data)
//            baseBean.setAction(action)
//            return baseBean
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }
}
