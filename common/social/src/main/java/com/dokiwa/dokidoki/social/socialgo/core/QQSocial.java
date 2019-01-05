package com.dokiwa.dokidoki.social.socialgo.core;

import android.app.Activity;
import android.text.TextUtils;

import com.dokiwa.dokidoki.social.R;
import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;
import com.tencent.tauth.Tencent;

/**
 * Created by Septenary
 */

public class QQSocial extends BaseSocial{

    protected Tencent tencent;

    protected QQSocial(Activity activity, String appId, SocialCallback callback) {
        super(activity, appId, callback);

        if (callback != null) {
            callback.setTarget(ISocial.TARGET_QQ);
        }

        if (TextUtils.isEmpty(appId)) {
            if (callback != null) {
                callback.fail(ISocial.ErrCode.ERR_APPID_EMPTY, "QQ's appId is empty!");
            }
            return;
        }

        tencent = Tencent.createInstance(appId, activity.getApplicationContext());
    }

    protected boolean unInitInterrupt() {
        if (tencent == null) {
            return true;
        }

        if (!tencent.isQQInstalled(activity)) {
            if (socialCallback != null) {
                socialCallback.fail(ISocial.ErrCode.ERR_NOT_INSTALLED, getString(R.string.social_uninstall_qq));
            }
            return true;
        }

        return false;
    }
}
