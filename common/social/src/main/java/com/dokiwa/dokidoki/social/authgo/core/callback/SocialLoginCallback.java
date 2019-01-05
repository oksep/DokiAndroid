package com.dokiwa.dokidoki.social.authgo.core.callback;

import com.dokiwa.dokidoki.social.authgo.core.entities.BaseUser;
import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;

/**
 * Created by Septenary
 */

public abstract class SocialLoginCallback extends SocialCallback{

    /**
     * 授权成功
     *
     * @param user
     */
    public abstract void success(BaseUser user);
}
