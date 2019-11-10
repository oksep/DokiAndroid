package com.dokiwa.dokidoki.login.api

import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import com.dokiwa.dokidoki.login.model.SocialListModel
import com.dokiwa.dokidoki.login.model.UserToken
import com.google.gson.JsonElement
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Septenary on 2018/12/3.
 */
interface LoginApi {

    @FormUrlEncoded
    @POST("/api/captcha/v1/send")
    fun getVerifyCode(@Field("phone") phoneNumber: String): Single<JsonElement>

    @FormUrlEncoded
    @POST("/api/auth/v1/token")
    fun loginByVerifyCode(
        @Field("x_phone_number") phoneNumber: String,
        @Field("x_phone_code") verifyCode: String,
        @Field("grant_type") grantType: String = "x-phone"
    ): Single<UserToken>

    @FormUrlEncoded
    @POST("/api/auth/v1/token")
    fun loginByVerifyCodeWithSocial(
        @Field("x_phone_social_type") socialType: String,
        @Field("x_phone_social_code") socialCode: String,
        @Field("x_phone_number") phoneNumber: String,
        @Field("x_phone_code") verifyCode: String,
        @Field("grant_type") grantType: String = "x-phone"
    ): Single<UserToken>

    @FormUrlEncoded
    @POST("/api/auth/v1/token")
    fun loginBySocial(
        @Field("x_social_code") socialCode: String,
        @Field("x_social_type") socialType: String,
        @Field("grant_type") grantType: String = "x-social"
    ): Single<UserToken>

    enum class XSocialType(val type: String) {
        QQ("qq"),
        Weibo("weibo"),
        Wechat("wechat");
    }

    @GET("/api/profile/v1/me")
    fun getLoginUserProfile(): Single<UserProfileWrap>

    @FormUrlEncoded
    @POST("/api/social/v1/bind")
    fun bindSocialAccount(
        @Field("type") type: String,
        @Field("code") code: String
    ): Single<JsonElement>

    @FormUrlEncoded
    @POST("/api/social/v1/unbind")
    fun unbindSocialAccount(
        @Field("type") type: String
    ): Single<JsonElement>

    @GET("/api/social/v1/list")
    fun getSocialAccountList(): Single<SocialListModel>

    @GET("/api/social/v1/info")
    fun getSocialAccountInfo(): Single<JsonElement>
}