package com.dokiwa.dokidoki.center.api.convert

import com.dokiwa.dokidoki.center.api.model.ApiData
import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ref: https://hackernoon.com/retrofit-converter-for-wrapped-responses-8919298a549c
 */
class CustomConverterFactory private constructor(gson: Gson) : Converter.Factory() {

    companion object {
        fun create(): CustomConverterFactory {
            return CustomConverterFactory(Gson())
        }
    }

    private val gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val wrappedType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> = arrayOf(type)
            override fun getOwnerType(): Type? = null
            override fun getRawType(): Type = ApiData::class.java
        }
        val gsonConverter: Converter<ResponseBody, *>? =
            gsonConverterFactory.responseBodyConverter(wrappedType, annotations, retrofit)
        return ResponseBodyConverter(gsonConverter as Converter<ResponseBody, ApiData<Any>>)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return gsonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }
}

class ResponseBodyConverter<T>(
    private val converter: Converter<ResponseBody, ApiData<T>>
) : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): T {
        val response = converter.convert(responseBody)
        // return if (response.status.code == 400001) {
        //      throw ApiException(response.statusCode, response.message?)
        // }
        return response.data
    }
}