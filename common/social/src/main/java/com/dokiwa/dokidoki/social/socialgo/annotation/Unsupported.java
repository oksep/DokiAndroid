package com.dokiwa.dokidoki.social.socialgo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Septenary
 */

@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Unsupported {

    /**
     * 暂不支持
     */
}
