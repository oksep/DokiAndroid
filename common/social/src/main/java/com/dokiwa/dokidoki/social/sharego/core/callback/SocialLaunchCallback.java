package com.dokiwa.dokidoki.social.sharego.core.callback;

import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;

/**
 * Created by Septenary
 */

public abstract class SocialLaunchCallback extends SocialCallback {

    /**
     * 唤起微信小程序成功回调
     *
     * @param extraData
     */
    public abstract void success(String extraData);
}
