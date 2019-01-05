package com.dokiwa.dokidoki.social.authgo.core.callback;

import com.dokiwa.dokidoki.social.authgo.core.entities.AuthResult;
import com.dokiwa.dokidoki.social.socialgo.core.callback.SocialCallback;

/**
 * Created by Septenary
 */

public abstract class SocialAuthCallback extends SocialCallback {

    /**
     * 授权成功
     *
     * @param authResult
     */
    public abstract void success(AuthResult authResult);

}
