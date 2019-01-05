package com.dokiwa.dokidoki.social.authgo.core;

import com.dokiwa.dokidoki.social.socialgo.core.ISocial;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Septenary
 */

public interface IAuth {

    OkHttpClient okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build();

    void auth();

    void login();

    interface ErrCode extends ISocial.ErrCode {

        //授权的ErrCode 从200开始
    }
}
