package com.dokiwa.dokidoki.social

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import com.dokiwa.dokidoki.social.authgo.core.AuthGo
import com.dokiwa.dokidoki.social.authgo.core.callback.SocialAuthCallback
import com.dokiwa.dokidoki.social.authgo.core.callback.SocialLoginCallback
import com.dokiwa.dokidoki.social.authgo.core.entities.AuthResult
import com.dokiwa.dokidoki.social.authgo.core.entities.BaseUser
import com.dokiwa.dokidoki.social.sharego.core.ShareGo
import com.dokiwa.dokidoki.social.sharego.core.callback.SocialShareCallback
import com.dokiwa.dokidoki.social.sharego.core.entities.ShareEntity
import com.dokiwa.dokidoki.social.socialgo.config.SocialConfig
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers


// qq这个：
// APP ID
// 1106924564
// APP KEY
// dd19be76ZzguYs7f

/**
 * Created by Septenary on 2019/1/6.
 */
object SocialHelper {

    fun initSocial(
        context: Context,
        qqAppId: String,
        wxAppId: String,
        wxSecretId: String,
        wbAppId: String,
        wbRedirectUrl: String
    ) {
        val builder = SocialConfig.Builder()
            .setQqAppId(qqAppId)
            .setWxAppId(wxAppId, wxSecretId)
            .setWbAppId(wbAppId, wbRedirectUrl)
            .build()
        SocialConfig.init(context.applicationContext, builder)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 认证
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun auth(context: Activity, type: SocialType): Single<String> {
        return Single.unsafeCreate<String> { emitter ->
            val callback = object : SocialAuthCallback() {
                override fun fail(errorCode: Int, defaultMsg: String?) {
                    emitter.onError(SocialAuthException(errorCode, defaultMsg))
                }

                override fun cancel() {
                    emitter.onError(SocialAuthException(0, "auth canceled"))
                }

                override fun success(authResult: AuthResult) {
                    emitter.onSuccess(authResult.code)
                }
            }

            when (type) {
                SocialType.WECHAT -> {
                    AuthGo.getInstance().authWX(context, callback)
                }
                SocialType.QQ -> {
                    AuthGo.getInstance().authQQ(context, callback)
                }
                SocialType.WEIBO -> {
                    AuthGo.getInstance().authWB(context, callback)
                }
            }
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread())
    }

    fun login(context: Activity, type: SocialType): Single<AuthUser> {
        return Single.unsafeCreate<AuthUser> { emitter ->
            val callback = object : SocialLoginCallback() {
                override fun fail(errorCode: Int, defaultMsg: String?) {
                    emitter.onError(SocialLoginException(errorCode, defaultMsg))
                }

                override fun cancel() {
                    emitter.onError(SocialLoginException(0, "login canceled"))
                }

                override fun success(user: BaseUser?) {
                    if (user == null) {
                        emitter.onError(SocialLoginException(0, "login user not get"))
                    } else {
                        emitter.onSuccess(user.toAuthUser())
                    }
                }
            }

            when (type) {
                SocialType.WECHAT -> {
                    AuthGo.getInstance().loginWX(context, callback)
                }
                SocialType.QQ -> {
                    AuthGo.getInstance().loginQQ(context, callback)
                }
                SocialType.WEIBO -> {
                    AuthGo.getInstance().loginWB(context, callback)
                }
            }
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread())
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 分享
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun shareText(context: Activity, type: SocialType, text: String): Single<Unit> {
        val shareEntity = ShareEntity.createTextInfo(ShareEntity.SHARE_TYPE_TEXT, text)
        return SocialHelper.shareEntity(context, type, shareEntity)
    }

    fun shareImage(context: Activity, type: SocialType, bitmap: Bitmap): Single<Unit> {
        val shareEntity = ShareEntity.createImageInfo(ShareEntity.SHARE_TYPE_IMG, bitmap)
        return SocialHelper.shareEntity(context, type, shareEntity)
    }

    fun shareWebPage(
        context: Activity,
        type: SocialType,
        title: String,
        summary: String,
        webUrl: String,
        icon: Bitmap
    ): Single<Unit> {
        val shareEntity = ShareEntity.createWebInfo(ShareEntity.SHARE_TYPE_WEB, title, summary, webUrl, icon)
        return SocialHelper.shareEntity(context, type, shareEntity)
    }

    fun shareEntity(context: Activity, type: SocialType, shareEntity: ShareEntity): Single<Unit> {
        return Single.unsafeCreate<Unit> { emitter ->
            val callback = obtainShareCallback(emitter)
            val shareGo = ShareGo.getInstance()
            when (type) {
                SocialType.WECHAT -> shareGo.shareWX(context, shareEntity, callback)
                SocialType.QQ -> shareGo.shareQQ(context, shareEntity, callback)
                SocialType.WEIBO -> shareGo.shareWB(context, shareEntity, callback)
            }
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread())
    }

    private fun obtainShareCallback(emitter: SingleObserver<in Unit>): SocialShareCallback {
        return object : SocialShareCallback() {
            override fun success() {
                emitter.onSuccess(Unit)
            }

            override fun fail(errorCode: Int, defaultMsg: String?) {
                emitter.onError(SocialShareException(errorCode, defaultMsg))
            }

            override fun cancel() {
                emitter.onError(SocialShareException(0, "share canceled"))
            }
        }
    }

    enum class SocialType { WEIBO, QQ, WECHAT }

    class SocialAuthException(errorCode: Int, message: String?) : Exception("code: $errorCode, message: $message")

    class SocialLoginException(errorCode: Int, message: String?) : Exception("code: $errorCode, message: $message")

    class SocialShareException(errorCode: Int, message: String?) : Exception("code: $errorCode, message: $message")

    class AuthUser : BaseUser()

    private fun BaseUser.toAuthUser(): AuthUser {
        return AuthUser().also {
            it.authResult = authResult
            it.nickName = nickName
            it.sex = sex
            it.headImageUrl = headImageUrl
            it.headImageUrlLarge = headImageUrlLarge
        }
    }
}