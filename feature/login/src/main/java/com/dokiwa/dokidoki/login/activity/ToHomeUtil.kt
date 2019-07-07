package com.dokiwa.dokidoki.login.activity

import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.rx.subscribeApiWithDialog
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.login.Log
import com.dokiwa.dokidoki.login.R
import com.dokiwa.dokidoki.login.api.LoginApi
import com.dokiwa.dokidoki.login.model.UserToken

/**
 * Created by Septenary on 2019/3/3.
 */
object ToHomeUtil {
    private const val TAG = "ToHomeUtil"

    fun ensureProfileThenToHome(userToken: UserToken, context: BaseActivity) {
        Api.resetAuthenticationToken(userToken.macKey, userToken.accessToken)
        Api.get(LoginApi::class.java)
            .getLoginUserProfile()
            .subscribeApiWithDialog(
                context,
                context,
                {
                    if (it.profile.gender == 0) {  // unknown gender
                        IProfilePlugin.get().launchCreateProfileActivity(context, userToken)
                    } else {
                        ILoginPlugin.get().onLoginDokiComplete(it.profile, userToken)
                        IHomePlugin.get().launchHomeActivity(context)
                    }
                    context.finishAffinity()
                },
                {
                    Log.e(TAG, "get user profile failed.", it)
                    context.toast(R.string.login_failed)
                    Api.resetAuthenticationToken(null, null)
                    context.finishAffinity()
                }
            )
    }
}