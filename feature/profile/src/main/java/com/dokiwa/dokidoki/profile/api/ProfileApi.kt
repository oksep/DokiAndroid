package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.plugin.model.Edu
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.MineProfile
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import io.reactivex.Single
import org.json.JSONException
import retrofit2.http.*

/**
 * Created by Septenary on 2019/3/2.
 */
interface ProfileApi {

    @FormUrlEncoded
    @POST("/api/profile/v1/update")
    fun createProfile(
        @Field("gender") gender: Int? = Gender.UNKNOWN,
        @Field("birthday") birthday: String? = "",
        @Field("height") height: Int? = 0,
        @Field("city_code") cityCode: String? = "",
        @Field("nickname") nickname: String? = "",
        @Field("avatar") avatar: String? = ""
    ): Single<UserProfileWrap>

    /**
    ### 更新自己的资料
    `POST` `/api/profile/v1/update`

    | 字段 | 类型 | 作用  |
    | --- | --- | --- |
    |  avatar | string | 头像 URL，上传规则 @see common.md 通用接口-上传图片  |
    |  nickname | string | 用户昵称  |
    |  gender | uint8 | 性别  |
    |  birthday | string | 用户生日，格式为 yyyymmdd，如 19901201  |
    |  height | uint16 | 身高，单位 cm  |
    |  education | uint8 | 学历  |
    |  city_code | string | 城市编码，@see common.md 通用接口-获取城市数据  |
    |  industry_id | uint32 | 行业 ID，@see common.md 通用接口-获取行业数据  |
    |  income | uint32 | 月收入  |
    |  intro | string | 个人简介  |
    |  pictures | string | 个人照片展示墙，多个照片间用 "," 分隔  |
    |  keywords | string | 个人关键词，多个关键词间用 "," 分隔，关键词只可以在关键词组中选择，@see common.md 通用接口-获取关键词组  |
     */
    @FormUrlEncoded
    @POST("/api/profile/v1/update")
    fun updateProfile(
        @Field("avatar") avatar: String? = "",
        @Field("nickname") nickname: String? = "",
        @Field("gender") gender: Int? = Gender.UNKNOWN,
        @Field("birthday") birthday: String? = "",
        @Field("height") height: Int? = 0,
        @Field("education") education: Int? = Edu.UNKOWN,
        @Field("city_code") cityCode: String? = "",
        @Field("industry_id") industryCode: Int? = 0,
        @Field("income") income: Int? = 0,
        @Field("intro") intro: String? = "",
        @Field("pictures") pictures: String? = "",
        @Field("keywords") keywords: String? = ""
    ): Single<UserProfileWrap>

    @GET("/api/profile/v1/user")
    fun getUserProfileById(@Query("user_id") userId: String): Single<UserProfileWrap>

    @GET("/api/profile/v1/user")
    fun getUserProfileByUUID(@Query("uuid") uuid: String): Single<UserProfileWrap>

    @GET("/api/search/v1/user")
    fun searchUser(@Query("kw") keyword: String): Single<SearchUserResultModel>

    @GET("/api/doki/v1/keyword-group")
    fun getTagsConfig(): Single<TagsGroupModel>

    @GET("/api/profile/v1/me")
    fun getMeProfile(): Single<MineProfile>

    @GET("/api/certification/v1/me")
    fun getCertification(): Single<CertificationWrap>

    @FormUrlEncoded
    @POST("/api/certification-identification/v1/update")
    fun updateCertifyId(
        @Field("name") name: String,
        @Field("number") number: String,
        @Field("image") image: String = ""
    ): Single<IdCertifyResult>

    @FormUrlEncoded
    @POST("/api/certification-education/v1/update")
    fun updateCertifyEducation(
        @Field("name") name: String,
        @Field("number") number: String,
        @Field("education") education: Int,
        @Field("graduation_year") graduationYear: Int,
        @Field("school") school: String = "",
        @Field("image") image: String = ""
    ): Single<JSONException>

    @GET("/api/setting/v1/me")
    fun getSettings(): Single<SettingModel>

    @FormUrlEncoded
    @POST("/api/setting/v1/update")
    fun updateSettingRealNameMsg(
        @Field("certificated_only") certificatedOnly: Boolean
    ): Single<SettingModel>

    @FormUrlEncoded
    @POST("/api/setting/v1/update")
    fun updateSettingAllowRecommend(
        @Field("allow_recommend") allowRecommend: Boolean
    ): Single<SettingModel>

    @GET("/api/user/v1/phone")
    fun getBindPhone(): Single<PhoneBindModel>
}