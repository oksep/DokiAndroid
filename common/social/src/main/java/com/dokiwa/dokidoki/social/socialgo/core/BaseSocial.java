package com.dokiwa.dokidoki.social.socialgo.core;

import android.app.Activity;

import androidx.annotation.StringRes;
import com.dokiwa.dokidoki.social.socialgo.config.SocialConfig;
import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;

/**
 * Create by gentriolee
 */

public class BaseSocial {

    protected SocialCallback socialCallback;
    protected Activity activity;
    protected String appId;

    protected BaseSocial(Activity activity, String appId, SocialCallback callback) {
        this.activity = activity;
        this.appId = appId;
        this.socialCallback = callback;
    }

    protected final String getString(@StringRes int resId) {
        return SocialConfig.getInstance().getContext().getString(resId);
    }
}
