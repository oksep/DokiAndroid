package com.dokiwa.dokidoki.social.socialgo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Septenary
 */

@Target(ElementType.PARAMETER)
public @interface ParamsRequired {
    /**
     * 必传参数
     */
}
