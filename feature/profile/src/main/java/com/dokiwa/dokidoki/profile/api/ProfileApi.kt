package com.dokiwa.dokidoki.profile.api

import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.model.UserProfileWrap
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Septenary on 2019/3/2.
 */
interface ProfileApi {

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
    fun createProfile(
        @Field("gender") gender: Int? = Gender.UNKNOWN,
        @Field("birthday") birthday: String? = "",
        @Field("height") height: Int? = 0,
        @Field("city_code") cityCode: String? = "",
        @Field("nickname") nickname: String? = "",

        // TODO: 2019/3/2 @Septenary avatar url
        @Field("avatar") avatar: String? = ""
    ): Single<UserProfileWrap>

    @GET("/api/profile/v1/user")
    fun getUserProfileById(@Query("user_id") userId: String): Single<UserProfileWrap>

    @GET("/api/profile/v1/user")
    fun getUserProfileByUUID(@Query("uuid") uuid: String): Single<UserProfileWrap>
}